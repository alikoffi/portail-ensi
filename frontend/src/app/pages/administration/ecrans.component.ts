import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EcranService } from '@/core/services/ecran.service';
import { NotificationService } from '@/core/services/notification.service';
import { Ecran } from '@/core/models/ecran.model';

/**
 * Visibilité des écrans. Masquer un écran le retire à tout le monde, y compris
 * aux rôles qui y avaient droit — et bloque aussi son API, pas seulement le menu.
 */
@Component({
  selector: 'app-ecrans',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ecrans.component.html'
})
export class EcransComponent implements OnInit {
  private readonly ecranService = inject(EcranService);
  private readonly notification = inject(NotificationService);

  readonly ecrans = signal<Ecran[]>([]);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);
  readonly enCours = signal<string | null>(null);

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.ecranService.lister().subscribe({
      next: (ecrans) => {
        this.ecrans.set(ecrans);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger la liste des écrans.');
        this.chargement.set(false);
      }
    });
  }

  basculer(ecran: Ecran): void {
    if (ecran.verrouille || this.enCours()) {
      return;
    }
    const cible = !ecran.visible;
    this.enCours.set(ecran.code);

    this.ecranService.changerVisibilite(ecran.code, cible).subscribe({
      next: (maj) => {
        this.enCours.set(null);
        this.ecrans.update((liste) => liste.map((e) => (e.code === maj.code ? maj : e)));
        this.notification.succes(
          cible ? `« ${ecran.libelle} » est de nouveau visible.` : `« ${ecran.libelle} » est masqué.`
        );
      },
      error: (e) => {
        this.enCours.set(null);
        this.notification.erreur(e?.error?.message ?? 'La modification a échoué.');
      }
    });
  }
}
