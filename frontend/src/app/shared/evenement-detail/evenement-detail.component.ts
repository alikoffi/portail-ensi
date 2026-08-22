import { Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentsComponent } from '@/shared/documents/documents.component';
import { ExportService } from '@/core/services/export.service';
import { PvService } from '@/core/services/pv.service';
import { NotificationService } from '@/core/services/notification.service';
import { Evenement, StatutEvenement, etatEvenement } from '@/core/models/evenement.model';
import { Pv } from '@/core/models/pv.model';

/**
 * Panneau de détail d'un évènement, partagé par le planning et le tableau de
 * bord : informations, statut, procès-verbal rattaché et pièces jointes.
 */
@Component({
  selector: 'app-evenement-detail',
  standalone: true,
  imports: [CommonModule, DocumentsComponent],
  templateUrl: './evenement-detail.component.html'
})
export class EvenementDetailComponent {
  private readonly pvService = inject(PvService);
  private readonly exportService = inject(ExportService);
  private readonly notification = inject(NotificationService);

  readonly evenement = input.required<Evenement>();
  readonly peutGerer = input(false);

  /** Masque les actions d'édition là où elles n'ont pas de sens (tableau de bord). */
  readonly actionsEdition = input(true);

  readonly fermer = output<void>();
  readonly modifier = output<Evenement>();
  readonly statutDemande = output<StatutEvenement>();
  readonly pvDemande = output<Evenement>();

  readonly pv = signal<Pv | null>(null);
  readonly chargementPv = signal(false);
  readonly exportEnCours = signal(false);

  readonly etat = computed(() => etatEvenement(this.evenement()));

  constructor() {
    effect(() => {
      const pvId = this.evenement().pvId;
      if (pvId == null) {
        this.pv.set(null);
        return;
      }
      this.chargementPv.set(true);
      this.pvService.detail(pvId).subscribe({
        next: (pv) => {
          this.pv.set(pv);
          this.chargementPv.set(false);
        },
        error: () => {
          this.pv.set(null);
          this.chargementPv.set(false);
        }
      });
    });
  }

  exporterPvPdf(): void {
    const pv = this.pv();
    if (!pv?.id) {
      return;
    }
    this.exportEnCours.set(true);
    this.exportService.pvPdf(pv.id).subscribe({
      next: (blob) => {
        this.exportService.telecharger(blob, `proces-verbal-${pv.id}.pdf`);
        this.exportEnCours.set(false);
      },
      error: () => {
        this.exportEnCours.set(false);
        this.notification.erreur("L'export PDF a échoué.");
      }
    });
  }
}
