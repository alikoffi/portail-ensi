import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Evenement, StatutEvenement } from '@/core/models/evenement.model';

@Injectable({ providedIn: 'root' })
export class EvenementService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws/evenement`;

  lister(): Observable<Evenement[]> {
    return this.http.get<Evenement[]>(`${this.base}/lister`);
  }

  enregistrer(evenement: Evenement): Observable<Evenement> {
    return this.http.post<Evenement>(`${this.base}/enregistrer`, evenement);
  }

  modifier(evenement: Evenement): Observable<Evenement> {
    return this.http.put<Evenement>(`${this.base}/modifier`, evenement);
  }

  /** Cloture, annule ou reporte un evenement. */
  changerStatut(id: number, statut: StatutEvenement): Observable<Evenement> {
    return this.http.put<Evenement>(`${this.base}/statut/${id}`, null, { params: { statut } });
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/supprimer/${id}`);
  }
}
