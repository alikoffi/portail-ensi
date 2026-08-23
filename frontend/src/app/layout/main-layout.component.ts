import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '@/core/services/auth.service';
import { EcranService } from '@/core/services/ecran.service';
import { CodeEcran } from '@/core/models/ecran.model';

interface ElementMenu {
  libelle: string;
  icone: string;
  route?: string;
  adminSeulement?: boolean;
  /** Écran correspondant : l'entrée disparaît si l'administrateur l'a masqué. */
  code?: CodeEcran;
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
  private readonly ecranService = inject(EcranService);

  readonly utilisateur = this.authService.user;
  readonly estAdmin = this.authService.estAdmin;
  readonly sidebarOuverte = signal(true);
  readonly menuProfilOuvert = signal(false);
  readonly annee = new Date().getFullYear();

  private readonly menuComplet: ElementMenu[] = [
    { libelle: 'Tableau de bord', icone: 'pi-home', route: '/tableau-de-bord', code: 'TABLEAU_DE_BORD' },
    { libelle: 'Planning', icone: 'pi-calendar', route: '/planning', code: 'PLANNING' },
    { libelle: 'États financiers', icone: 'pi-wallet', route: '/finances', code: 'FINANCES' },
    { libelle: 'Membres', icone: 'pi-users', route: '/membres', code: 'MEMBRES' },
    { libelle: 'Cotisations', icone: 'pi-money-bill', route: '/cotisations', code: 'COTISATIONS' },
    { libelle: 'Procès-verbaux', icone: 'pi-file', route: '/proces-verbaux', code: 'PROCES_VERBAUX' },
    { libelle: 'Comptes', icone: 'pi-shield', route: '/administration/comptes', adminSeulement: true, code: 'COMPTES' },
    {
      libelle: 'Paramétrage',
      icone: 'pi-cog',
      adminSeulement: true,
      enfants: [
        { libelle: 'Écrans', icone: 'pi-eye', route: '/administration/ecrans', code: 'ECRANS' },
        { libelle: "Types d'évènement", icone: 'pi-calendar', route: '/administration/parametrage/TYPE_EVENEMENT', code: 'PARAMETRAGE' },
        { libelle: 'Catégories de transaction', icone: 'pi-wallet', route: '/administration/parametrage/CATEGORIE_TRANSACTION', code: 'PARAMETRAGE' },
        { libelle: 'Spécialités', icone: 'pi-bookmark', route: '/administration/parametrage/SPECIALITE', code: 'PARAMETRAGE' }
      ]
    }
  ];

  /**
   * Menu réellement affiché : le rôle décide de ce que l'utilisateur a le droit
   * de voir, puis le masquage administrateur retire l'entrée à tout le monde.
   * Un groupe dont tous les enfants sont masqués disparaît lui aussi.
   */
  readonly menu = computed<ElementMenu[]>(() => {
    this.ecranService.ecrans();
    const retenu = (element: ElementMenu): boolean =>
      (!element.adminSeulement || this.estAdmin()) &&
      (!element.code || this.ecranService.estVisible(element.code));

    return this.menuComplet
      .filter(retenu)
      .map((element) =>
        element.enfants ? { ...element, enfants: element.enfants.filter(retenu) } : element
      )
      .filter((element) => !element.enfants || element.enfants.length > 0);
  });

  /** Vrai pour un écran que l'admin voit encore mais qui est retiré aux autres. */
  estMasque(element: ElementMenu): boolean {
    return element.code ? this.ecranService.estMasquePourLesAutres(element.code) : false;
  }

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

  basculerMenuProfil(): void {
    this.menuProfilOuvert.update((v) => !v);
  }

  fermerMenuProfil(): void {
    this.menuProfilOuvert.set(false);
  }

  deconnexion(): void {
    this.menuProfilOuvert.set(false);
    this.authService.logout();
  }
}
