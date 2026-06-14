import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

export interface RappelResultat {
  nombreEvenements: number;
  nombreMembresEnRetard: number;
  nombreDestinataires: number;
}

@Injectable({ providedIn: 'root' })
export class RappelService {
  private readonly http = inject(HttpClient);

  envoyer(): Observable<RappelResultat> {
    return this.http.post<RappelResultat>(`${environment.apiUrl}/ws/rappel/envoyer`, {});
  }
}
