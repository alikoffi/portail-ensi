import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { AuthService } from '@/core/services/auth.service';
import { UtilisateurService } from '@/core/services/utilisateur.service';
import { NotificationService } from '@/core/services/notification.service';

const LIBELLES_ROLE: Record<string, string> = {
  ADMIN: 'Administrateur',
  TRESORIER: 'Trésorier',
  SECRETAIRE: 'Secrétaire',
  VIEWER: 'Visiteur'
};

function memeMotDePasse(control: AbstractControl): ValidationErrors | null {
  const nouveau = control.get('nouveauMotDePasse')?.value;
  const confirmation = control.get('confirmation')?.value;
  return nouveau && confirmation && nouveau !== confirmation ? { differents: true } : null;
}

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profil.component.html'
})
export class ProfilComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly utilisateurService = inject(UtilisateurService);
  private readonly notification = inject(NotificationService);

  readonly utilisateur = this.authService.user;
  readonly libelleRole = computed(() => LIBELLES_ROLE[this.utilisateur()?.role ?? ''] ?? this.utilisateur()?.role);
  readonly initiales = computed(() => {
    const base = this.utilisateur()?.label ?? this.utilisateur()?.username ?? '?';
    return base.split(' ').map((m) => m.charAt(0)).join('').substring(0, 2).toUpperCase();
  });

  readonly enregistrementProfil = signal(false);
  readonly enregistrementMdp = signal(false);
  readonly afficher = signal(false);

  readonly formProfil = this.fb.nonNullable.group({
    label: [''],
    email: ['', Validators.email]
  });

  readonly formMotDePasse = this.fb.nonNullable.group(
    {
      ancienMotDePasse: ['', Validators.required],
      nouveauMotDePasse: ['', [Validators.required, Validators.minLength(6)]],
      confirmation: ['', Validators.required]
    },
    { validators: memeMotDePasse }
  );

  ngOnInit(): void {
    // Le token de connexion ne porte pas l'email : on le charge depuis l'API.
    this.utilisateurService.moi().subscribe({
      next: (u) => this.formProfil.patchValue({ label: u.label ?? '', email: u.email ?? '' })
    });
  }

  basculerAffichage(): void {
    this.afficher.update((v) => !v);
  }

  enregistrerProfil(): void {
    if (this.formProfil.invalid) {
      this.formProfil.markAllAsTouched();
      return;
    }
    this.enregistrementProfil.set(true);
    const v = this.formProfil.getRawValue();
    this.utilisateurService.modifierProfil({ label: v.label || undefined, email: v.email || undefined }).subscribe({
      next: (u) => {
        this.enregistrementProfil.set(false);
        this.authService.majLabelLocal(u.label ?? '');
        this.notification.succes('Profil mis à jour.');
      },
      error: () => {
        this.enregistrementProfil.set(false);
        this.notification.erreur('La mise à jour a échoué.');
      }
    });
  }

  changerMotDePasse(): void {
    if (this.formMotDePasse.invalid) {
      this.formMotDePasse.markAllAsTouched();
      return;
    }
    this.enregistrementMdp.set(true);
    const v = this.formMotDePasse.getRawValue();
    this.utilisateurService.changerMotDePasse({ ancienMotDePasse: v.ancienMotDePasse, nouveauMotDePasse: v.nouveauMotDePasse }).subscribe({
      next: () => {
        this.enregistrementMdp.set(false);
        this.formMotDePasse.reset({ ancienMotDePasse: '', nouveauMotDePasse: '', confirmation: '' });
        this.notification.succes('Mot de passe modifié.');
      },
      error: (err) => {
        this.enregistrementMdp.set(false);
        this.notification.erreur(err?.status === 400 ? "L'ancien mot de passe est incorrect." : 'Le changement a échoué.');
      }
    });
  }
}
