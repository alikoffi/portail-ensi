import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '@/core/services/auth.service';
import { BilanService } from '@/core/services/bilan.service';
import { ExportService } from '@/core/services/export.service';
import { NotificationService } from '@/core/services/notification.service';
import { Bilan, RubriqueBilan, SectionBilan } from '@/core/models/bilan.model';

@Component({
  selector: 'app-bilan-tab',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './bilan-tab.component.html'
})
export class BilanTabComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly bilanService = inject(BilanService);
  private readonly exportService = inject(ExportService);
  private readonly authService = inject(AuthService);
  private readonly notification = inject(NotificationService);

  readonly peutGerer = this.authService.peutGererFinances;
  readonly exportEnCours = signal(false);

  readonly bilan = signal<Bilan | null>(null);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);

  readonly modalOuvert = signal(false);
  readonly sectionCourante = signal<SectionBilan>('actif');
  readonly enregistrement = signal(false);
  readonly erreurFormulaire = signal<string | null>(null);

  readonly aSupprimer = signal<{ section: SectionBilan; rubrique: RubriqueBilan } | null>(null);
  readonly suppressionEnCours = signal(false);

  readonly form = this.fb.nonNullable.group({
    rubrique: ['', Validators.required],
    montant: this.fb.control<number | null>(null, [Validators.required, Validators.min(0.01)])
  });

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.bilanService.resume().subscribe({
      next: (b) => {
        this.bilan.set(b);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger le bilan.');
        this.chargement.set(false);
      }
    });
  }

  formater(montant: number | undefined): string {
    if (montant === undefined || montant === null) {
      return '— FCFA';
    }
    return new Intl.NumberFormat('fr-FR').format(montant) + ' FCFA';
  }

  exporterPdf(): void {
    this.exportEnCours.set(true);
    this.exportService.bilanPdf().subscribe({
      next: (blob) => {
        this.exportService.telecharger(blob, 'bilan.pdf');
        this.exportEnCours.set(false);
        this.notification.succes('Export PDF généré.');
      },
      error: () => {
        this.exportEnCours.set(false);
        this.notification.erreur("L'export PDF a échoué.");
      }
    });
  }

  ouvrirAjout(section: SectionBilan): void {
    this.sectionCourante.set(section);
    this.erreurFormulaire.set(null);
    this.form.reset({ rubrique: '', montant: null });
    this.modalOuvert.set(true);
  }

  fermerModal(): void {
    this.modalOuvert.set(false);
  }

  enregistrer(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enregistrement.set(true);
    this.erreurFormulaire.set(null);

    const v = this.form.getRawValue();
    const payload: RubriqueBilan = { rubrique: v.rubrique, montant: v.montant as number };

    this.bilanService.enregistrer(this.sectionCourante(), payload).subscribe({
      next: () => {
        this.enregistrement.set(false);
        this.modalOuvert.set(false);
        this.notification.succes('Rubrique ajoutée.');
        this.charger();
      },
      error: () => {
        this.enregistrement.set(false);
        this.erreurFormulaire.set("L'enregistrement a échoué.");
      }
    });
  }

  demanderSuppression(section: SectionBilan, rubrique: RubriqueBilan): void {
    this.aSupprimer.set({ section, rubrique });
  }

  annulerSuppression(): void {
    this.aSupprimer.set(null);
  }

  confirmerSuppression(): void {
    const cible = this.aSupprimer();
    if (!cible?.rubrique.id) {
      return;
    }
    this.suppressionEnCours.set(true);
    this.bilanService.supprimer(cible.section, cible.rubrique.id).subscribe({
      next: () => {
        this.suppressionEnCours.set(false);
        this.aSupprimer.set(null);
        this.notification.succes('Rubrique supprimée.');
        this.charger();
      },
      error: () => {
        this.suppressionEnCours.set(false);
        this.aSupprimer.set(null);
        this.notification.erreur('La suppression a échoué.');
      }
    });
  }
}
