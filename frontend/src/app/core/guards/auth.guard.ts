import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '@/core/services/auth.service';

/** Autorise l'acces aux utilisateurs connectes. */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.estConnecte()) {
    return true;
  }
  return router.parseUrl('/connexion');
};

/** Autorise l'acces aux administrateurs uniquement. */
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.estAdmin()) {
    return true;
  }
  return router.parseUrl('/');
};
