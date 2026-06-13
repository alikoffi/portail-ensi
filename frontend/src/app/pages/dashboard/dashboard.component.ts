import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '@/core/services/auth.service';
import { TableauBordService } from '@/core/services/tableau-bord.service';
import { TableauBord } from '@/core/models/tableau-bord.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly tableauBordService = inject(TableauBordService);

  readonly utilisateur = this.authService.user;
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);
  readonly donnees = signal<TableauBord | null>(null);

  readonly indicateurs = computed(() => {
    const d = this.donnees();
    return [
      { libelle: 'Solde actuel', valeur: this.formaterMontant(d?.solde), icone: 'pi-wallet', accent: 'bg-ensi-50 text-ensi-700' },
      { libelle: 'Recettes totales', valeur: this.formaterMontant(d?.recettesTotales), icone: 'pi-arrow-down-left', accent: 'bg-emerald-50 text-emerald-700' },
      { libelle: 'Dépenses totales', valeur: this.formaterMontant(d?.depensesTotales), icone: 'pi-arrow-up-right', accent: 'bg-rose-50 text-rose-700' },
      { libelle: 'Évènements ce mois', valeur: d ? String(d.evenementsCeMois) : '—', icone: 'pi-calendar', accent: 'bg-amber-50 text-amber-700' }
    ];
  });

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.tableauBordService.resume().subscribe({
      next: (d) => {
        this.donnees.set(d);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger le tableau de bord.');
        this.chargement.set(false);
      }
    });
  }

  formaterMontant(valeur: number | undefined): string {
    if (valeur === undefined || valeur === null) {
      return '—';
    }
    return new Intl.NumberFormat('fr-FR').format(valeur) + ' FCFA';
  }
}
