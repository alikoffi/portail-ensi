import { Routes } from '@angular/router';
import { authGuard, adminGuard, ecranGuard } from '@/core/guards/auth.guard';
import { MainLayoutComponent } from '@/layout/main-layout.component';

export const routes: Routes = [
  {
    path: 'connexion',
    loadComponent: () => import('@/pages/login/login.component').then((m) => m.LoginComponent)
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'tableau-de-bord', pathMatch: 'full' },
      {
        path: 'tableau-de-bord',
        loadComponent: () =>
          import('@/pages/dashboard/dashboard.component').then((m) => m.DashboardComponent)
      },
      {
        path: 'planning',
        canActivate: [ecranGuard],
        data: { ecran: 'PLANNING' },
        loadComponent: () =>
          import('@/pages/planning/planning.component').then((m) => m.PlanningComponent)
      },
      {
        path: 'finances',
        canActivate: [ecranGuard],
        data: { ecran: 'FINANCES' },
        loadComponent: () =>
          import('@/pages/finances/finances.component').then((m) => m.FinancesComponent)
      },
      {
        path: 'proces-verbaux',
        canActivate: [ecranGuard],
        data: { ecran: 'PROCES_VERBAUX' },
        loadComponent: () =>
          import('@/pages/proces-verbaux/proces-verbaux.component').then((m) => m.ProcesVerbauxComponent)
      },
      {
        path: 'membres',
        canActivate: [ecranGuard],
        data: { ecran: 'MEMBRES' },
        loadComponent: () => import('@/pages/membres/membres.component').then((m) => m.MembresComponent)
      },
      {
        path: 'cotisations',
        canActivate: [ecranGuard],
        data: { ecran: 'COTISATIONS' },
        loadComponent: () => import('@/pages/cotisations/cotisations.component').then((m) => m.CotisationsComponent)
      },
      {
        path: 'administration/comptes',
        canActivate: [adminGuard],
        loadComponent: () => import('@/pages/administration/comptes.component').then((m) => m.ComptesComponent)
      },
      {
        path: 'administration/ecrans',
        canActivate: [adminGuard],
        loadComponent: () => import('@/pages/administration/ecrans.component').then((m) => m.EcransComponent)
      },
      {
        path: 'administration/parametrage/:categorie',
        canActivate: [adminGuard],
        loadComponent: () => import('@/pages/administration/parametrage.component').then((m) => m.ParametrageComponent)
      },
      {
        path: 'profil',
        loadComponent: () => import('@/pages/profil/profil.component').then((m) => m.ProfilComponent)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
