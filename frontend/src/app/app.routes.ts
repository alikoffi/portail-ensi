import { Routes } from '@angular/router';
import { authGuard } from '@/core/guards/auth.guard';
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
        loadComponent: () =>
          import('@/pages/planning/planning.component').then((m) => m.PlanningComponent)
      },
      {
        path: 'finances',
        loadComponent: () =>
          import('@/pages/finances/finances.component').then((m) => m.FinancesComponent)
      },
      {
        path: 'proces-verbaux',
        loadComponent: () =>
          import('@/pages/proces-verbaux/proces-verbaux.component').then((m) => m.ProcesVerbauxComponent)
      },
      {
        path: 'membres',
        loadComponent: () => import('@/pages/membres/membres.component').then((m) => m.MembresComponent)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
