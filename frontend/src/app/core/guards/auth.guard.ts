import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from '@/core/services/auth.service';
import { EcranService } from '@/core/services/ecran.service';
import { CodeEcran } from '@/core/models/ecran.model';

/**
 * Autorise l'acces aux utilisateurs connectes, et charge au passage la liste
 * des ecrans visibles : c'est le seul point par lequel toute navigation passe,
 * y compris apres un rechargement de page.
 */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const ecranService = inject(EcranService);
  const router = inject(Router);

  if (!authService.estConnecte()) {
    return router.parseUrl('/connexion');
  }
  if (ecranService.chargee()) {
    return true;
  }
  return ecranService.charger().pipe(
    map(() => true),
    // Un echec de chargement ne doit pas condamner l'acces : le serveur reste
    // de toute facon la garde reelle sur les ecrans masques.
    catchError(() => of(true))
  );
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

/**
 * Refuse un ecran masque par l'administrateur, meme si le role l'autorisait.
 * Le code attendu est declare dans `data.ecran` de la route.
 */
export const ecranGuard: CanActivateFn = (route) => {
  const ecranService = inject(EcranService);
  const router = inject(Router);

  const code = route.data['ecran'] as CodeEcran | undefined;
  if (!code || ecranService.estVisible(code)) {
    return true;
  }
  // Le tableau de bord est verrouille : il ne peut pas etre masque, donc pas de boucle.
  return router.parseUrl('/tableau-de-bord');
};
