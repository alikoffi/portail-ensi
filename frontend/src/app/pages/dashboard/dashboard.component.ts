import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '@/core/services/auth.service';

interface Indicateur {
  libelle: string;
  valeur: string;
  icone: string;
  accent: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent {
  private readonly authService = inject(AuthService);
  readonly utilisateur = this.authService.user;

  // Données fictives — seront branchées sur l'API au Lot 1.
  readonly indicateurs: Indicateur[] = [
    { libelle: 'Solde actuel', valeur: '— FCFA', icone: 'pi-wallet', accent: 'bg-ensi-50 text-ensi-700' },
    { libelle: 'Recettes totales', valeur: '— FCFA', icone: 'pi-arrow-down-left', accent: 'bg-emerald-50 text-emerald-700' },
    { libelle: 'Dépenses totales', valeur: '— FCFA', icone: 'pi-arrow-up-right', accent: 'bg-rose-50 text-rose-700' },
    { libelle: 'Évènements ce mois', valeur: '—', icone: 'pi-calendar', accent: 'bg-amber-50 text-amber-700' }
  ];

  readonly modules = [
    { titre: 'Planning', desc: 'Évènements et agenda de la promotion', icone: 'pi-calendar', lot: 'Lot 2' },
    { titre: 'États financiers', desc: 'Transactions, journal et bilan', icone: 'pi-wallet', lot: 'Lot 3' },
    { titre: 'Procès-verbaux', desc: 'Archivage des réunions du bureau', icone: 'pi-file', lot: 'Lot 4' }
  ];
}
