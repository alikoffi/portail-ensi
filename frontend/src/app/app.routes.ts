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
      }
      // Lots suivants : planning, finances, proces-verbaux
    ]
  },
  { path: '**', redirectTo: '' }
];
