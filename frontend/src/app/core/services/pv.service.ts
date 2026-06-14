import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Pv } from '@/core/models/pv.model';

@Injectable({ providedIn: 'root' })
export class PvService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws/pv`;

  lister(): Observable<Pv[]> {
    return this.http.get<Pv[]>(`${this.base}/lister`);
  }

  enregistrer(pv: Pv): Observable<Pv> {
    return this.http.post<Pv>(`${this.base}/enregistrer`, pv);
  }

  modifier(pv: Pv): Observable<Pv> {
    return this.http.put<Pv>(`${this.base}/modifier`, pv);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/supprimer/${id}`);
  }
}
