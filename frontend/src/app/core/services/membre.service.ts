import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Cotisation, Membre, Recouvrement } from '@/core/models/membre.model';

@Injectable({ providedIn: 'root' })
export class MembreService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws`;

  lister(): Observable<Membre[]> {
    return this.http.get<Membre[]>(`${this.base}/membre/lister`);
  }

  recouvrement(): Observable<Recouvrement> {
    return this.http.get<Recouvrement>(`${this.base}/membre/recouvrement`);
  }

  enregistrer(membre: Membre): Observable<Membre> {
    return this.http.post<Membre>(`${this.base}/membre/enregistrer`, membre);
  }

  modifier(membre: Membre): Observable<Membre> {
    return this.http.put<Membre>(`${this.base}/membre/modifier`, membre);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/membre/supprimer/${id}`);
  }

  // ----- Cotisations -----
  cotisationsParMembre(membreId: number): Observable<Cotisation[]> {
    return this.http.get<Cotisation[]>(`${this.base}/cotisation/membre/${membreId}`);
  }

  enregistrerCotisation(cotisation: Cotisation): Observable<Cotisation> {
    return this.http.post<Cotisation>(`${this.base}/cotisation/enregistrer`, cotisation);
  }

  supprimerCotisation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/cotisation/supprimer/${id}`);
  }
}
