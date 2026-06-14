import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '@environments/environment';
import { LoginRequest, LoginResponse, RoleUtilisateur } from '@/core/models/utilisateur.model';

const TOKEN_KEY = 'portail_ensi_token';
const USER_KEY = 'portail_ensi_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  /** Utilisateur courant (signal réactif). */
  private readonly _user = signal<LoginResponse | null>(this.lireUtilisateurStocke());
  readonly user = this._user.asReadonly();
  readonly estConnecte = computed(() => this._user() !== null);
  readonly role = computed(() => this._user()?.role ?? null);
  readonly estAdmin = computed(() => this.role() === 'ADMIN');

  /** Finances (transactions, bilan) et cotisations : ADMIN ou TRESORIER. */
  readonly peutGererFinances = computed(() => this.role() === 'ADMIN' || this.role() === 'TRESORIER');

  /** Planning : ADMIN ou SECRETAIRE. */
  readonly peutGererPlanning = computed(() => this.role() === 'ADMIN' || this.role() === 'SECRETAIRE');

  /** Procès-verbaux : ADMIN ou SECRETAIRE. */
  readonly peutGererPv = computed(() => this.role() === 'ADMIN' || this.role() === 'SECRETAIRE');

  /** Membres : ADMIN ou SECRETAIRE. */
  readonly peutGererMembres = computed(() => this.role() === 'ADMIN' || this.role() === 'SECRETAIRE');

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/ws/auth/login`, credentials).pipe(
      tap((reponse) => {
        localStorage.setItem(TOKEN_KEY, reponse.token);
        localStorage.setItem(USER_KEY, JSON.stringify(reponse));
        this._user.set(reponse);
      })
    );
  }

  /** Met à jour le libellé affiché localement (après modification du profil). */
  majLabelLocal(label: string): void {
    const courant = this._user();
    if (courant) {
      const maj = { ...courant, label };
      localStorage.setItem(USER_KEY, JSON.stringify(maj));
      this._user.set(maj);
    }
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._user.set(null);
    this.router.navigate(['/connexion']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  aRole(role: RoleUtilisateur): boolean {
    return this._user()?.role === role;
  }

  private lireUtilisateurStocke(): LoginResponse | null {
    const brut = localStorage.getItem(USER_KEY);
    return brut ? (JSON.parse(brut) as LoginResponse) : null;
  }
}
