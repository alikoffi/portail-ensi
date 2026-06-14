import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '@/core/services/auth.service';

interface ElementMenu {
  libelle: string;
  icone: string;
  route?: string;
  adminSeulement?: boolean;
  enfants?: ElementMenu[];
}

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './main-layout.component.html'
})
export class MainLayoutComponent {
  private readonly authService = inject(AuthService);

  readonly utilisateur = this.authService.user;
  readonly estAdmin = this.authService.estAdmin;
  readonly sidebarOuverte = signal(true);
  readonly annee = new Date().getFullYear();

  readonly menu: ElementMenu[] = [
    { libelle: 'Tableau de bord', icone: 'pi-home', route: '/tableau-de-bord' },
    { libelle: 'Planning', icone: 'pi-calendar', route: '/planning' },
    { libelle: 'États financiers', icone: 'pi-wallet', route: '/finances' },
    { libelle: 'Membres', icone: 'pi-users', route: '/membres' },
    { libelle: 'Procès-verbaux', icone: 'pi-file', route: '/proces-verbaux' },
    { libelle: 'Comptes', icone: 'pi-shield', route: '/administration/comptes', adminSeulement: true },
    {
      libelle: 'Paramétrage',
      icone: 'pi-cog',
      adminSeulement: true,
      enfants: [
        { libelle: "Types d'évènement", icone: 'pi-calendar', route: '/administration/parametrage/TYPE_EVENEMENT' },
        { libelle: 'Catégories de transaction', icone: 'pi-wallet', route: '/administration/parametrage/CATEGORIE_TRANSACTION' },
        { libelle: 'Spécialités', icone: 'pi-bookmark', route: '/administration/parametrage/SPECIALITE' }
      ]
    }
  ];

  readonly groupesOuverts = signal<Set<string>>(new Set());

  estGroupeOuvert(libelle: string): boolean {
    return this.groupesOuverts().has(libelle);
  }

  basculerGroupe(libelle: string): void {
    this.groupesOuverts.update((set) => {
      const copie = new Set(set);
      copie.has(libelle) ? copie.delete(libelle) : copie.add(libelle);
      return copie;
    });
  }

  readonly initiales = computed(() => {
    const label = this.utilisateur()?.label ?? this.utilisateur()?.username ?? '?';
    return label
      .split(' ')
      .map((m) => m.charAt(0))
      .join('')
      .substring(0, 2)
      .toUpperCase();
  });

  basculerSidebar(): void {
    this.sidebarOuverte.update((v) => !v);
  }

  deconnexion(): void {
    this.authService.logout();
  }
}
