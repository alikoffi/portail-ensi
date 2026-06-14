import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { UtilisateurService } from '@/core/services/utilisateur.service';
import { RoleUtilisateur, Utilisateur } from '@/core/models/utilisateur.model';

const LIBELLES_ROLE: Record<RoleUtilisateur, string> = {
  ADMIN: 'Administrateur',
  TRESORIER: 'Trésorier',
  SECRETAIRE: 'Secrétaire',
  VIEWER: 'Visiteur'
};

@Component({
  selector: 'app-comptes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableModule],
  templateUrl: './comptes.component.html'
})
export class ComptesComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly utilisateurService = inject(UtilisateurService);

  readonly utilisateurs = signal<Utilisateur[]>([]);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);

  readonly modalOuvert = signal(false);
  readonly enregistrement = signal(false);
  readonly erreurFormulaire = signal<string | null>(null);
  readonly afficherMotDePasse = signal(false);

  readonly roles: RoleUtilisateur[] = ['ADMIN', 'TRESORIER', 'SECRETAIRE', 'VIEWER'];

  readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(6)]],
    role: ['VIEWER' as RoleUtilisateur, Validators.required],
    label: ['']
  });

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.utilisateurService.lister().subscribe({
      next: (us) => {
        this.utilisateurs.set(us);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger les comptes.');
        this.chargement.set(false);
      }
    });
  }

  libelleRole(role: string): string {
    return LIBELLES_ROLE[role as RoleUtilisateur] ?? role;
  }

  couleurRole(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'bg-ensi-50 text-ensi-700';
      case 'TRESORIER':
        return 'bg-amber-50 text-amber-700';
      case 'SECRETAIRE':
        return 'bg-blue-50 text-blue-700';
      default:
        return 'bg-gray-100 text-gray-500';
    }
  }

  ouvrirCreation(): void {
    this.erreurFormulaire.set(null);
    this.afficherMotDePasse.set(false);
    this.form.reset({ username: '', password: '', role: 'VIEWER', label: '' });
    this.modalOuvert.set(true);
  }

  fermerModal(): void {
    this.modalOuvert.set(false);
  }

  basculerMotDePasse(): void {
    this.afficherMotDePasse.update((v) => !v);
  }

  enregistrer(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enregistrement.set(true);
    this.erreurFormulaire.set(null);
    const v = this.form.getRawValue();
    this.utilisateurService.enregistrer({ username: v.username, password: v.password, role: v.role, label: v.label || undefined }).subscribe({
      next: () => {
        this.enregistrement.set(false);
        this.modalOuvert.set(false);
        this.charger();
      },
      error: (err) => {
        this.enregistrement.set(false);
        this.erreurFormulaire.set(err?.status === 409 ? "Ce nom d'utilisateur existe déjà." : "L'enregistrement a échoué.");
      }
    });
  }

  basculerActif(u: Utilisateur): void {
    this.utilisateurService.basculerActif(u.id).subscribe({
      next: () => this.charger(),
      error: () => this.erreur.set('La mise à jour a échoué.')
    });
  }
}
