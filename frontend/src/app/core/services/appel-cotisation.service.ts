import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { AppelCotisation, AppelDetail, PaiementSaisie } from '@/core/models/appel-cotisation.model';
import { StatistiqueCotisation } from '@/core/models/membre.model';

@Injectable({ providedIn: 'root' })
export class AppelCotisationService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws/appel-cotisation`;

  lister(): Observable<AppelCotisation[]> {
    return this.http.get<AppelCotisation[]>(`${this.base}/lister`);
  }

  detail(id: number): Observable<AppelDetail> {
    return this.http.get<AppelDetail>(`${this.base}/detail/${id}`);
  }

  enregistrer(appel: AppelCotisation): Observable<AppelCotisation> {
    return this.http.post<AppelCotisation>(`${this.base}/enregistrer`, appel);
  }

  modifier(appel: AppelCotisation): Observable<AppelCotisation> {
    return this.http.put<AppelCotisation>(`${this.base}/modifier`, appel);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/supprimer/${id}`);
  }

  enregistrerPaiement(paiement: PaiementSaisie): Observable<void> {
    return this.http.post<void>(`${this.base}/paiement`, paiement);
  }

  supprimerPaiement(appelId: number, membreId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/paiement/${appelId}/${membreId}`);
  }

  statistiques(annee?: number): Observable<StatistiqueCotisation> {
    const url = `${this.base}/statistiques${annee ? `?annee=${annee}` : ''}`;
    return this.http.get<StatistiqueCotisation>(url);
  }
}
