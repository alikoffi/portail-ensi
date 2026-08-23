import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '@environments/environment';
import { CodeEcran, Ecran } from '@/core/models/ecran.model';

/**
 * Visibilité des écrans décidée par l'administrateur.
 *
 * La liste des écrans visibles est chargée une fois puis conservée : le menu et
 * le garde de route la consultent à chaque navigation.
 */
@Injectable({ providedIn: 'root' })
export class EcranService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws/ecran`;

  private readonly _ecrans = signal<Ecran[] | null>(null);

  /** null tant que la liste n'est pas revenue. */
  readonly ecrans = this._ecrans.asReadonly();
  readonly chargee = computed(() => this._ecrans() !== null);

  /** Recharge la liste des écrans visibles (connexion, ou après modification). */
  charger(): Observable<Ecran[]> {
    return this.http
      .get<Ecran[]>(`${this.base}/visibles`)
      .pipe(tap((ecrans) => this._ecrans.set(ecrans)));
  }

  /**
   * Liste absente (chargement en échec) : on ne masque rien. Le filtre décisif
   * vit côté serveur, le menu ne doit pas se vider sur un incident réseau.
   */
  estVisible(code: CodeEcran): boolean {
    const ecrans = this._ecrans();
    return ecrans === null ? true : ecrans.some((e) => e.code === code);
  }

  /**
   * Écran que l'administrateur a retiré aux autres rôles. Lui continue de le
   * voir : ce drapeau sert à le lui signaler dans son menu.
   */
  estMasquePourLesAutres(code: CodeEcran): boolean {
    return this._ecrans()?.some((e) => e.code === code && !e.visible) ?? false;
  }

  /** Vide le cache à la déconnexion : le suivant peut avoir une autre configuration. */
  reinitialiser(): void {
    this._ecrans.set(null);
  }

  // ---------- Administration ----------
  lister(): Observable<Ecran[]> {
    return this.http.get<Ecran[]>(`${this.base}/lister`);
  }

  changerVisibilite(code: CodeEcran, visible: boolean): Observable<Ecran> {
    return this.http
      .put<Ecran>(`${this.base}/visibilite/${code}`, null, { params: { visible } })
      .pipe(tap(() => this.charger().subscribe()));
  }
}
