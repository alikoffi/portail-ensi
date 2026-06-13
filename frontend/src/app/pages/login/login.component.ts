import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '@/core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly chargement = signal(false);
  readonly erreur = signal<string | null>(null);
  readonly afficherMotDePasse = signal(false);
  readonly annee = new Date().getFullYear();

  readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  basculerMotDePasse(): void {
    this.afficherMotDePasse.update((v) => !v);
  }

  soumettre(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.chargement.set(true);
    this.erreur.set(null);

    this.authService.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.chargement.set(false);
        this.router.navigate(['/tableau-de-bord']);
      },
      error: (err) => {
        this.chargement.set(false);
        this.erreur.set(
          err?.status === 401
            ? 'Nom d’utilisateur ou mot de passe incorrect.'
            : 'Connexion impossible. Réessayez plus tard.'
        );
      }
    });
  }
}
