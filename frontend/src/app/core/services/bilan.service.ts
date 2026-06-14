import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Bilan, RubriqueBilan, SectionBilan } from '@/core/models/bilan.model';

@Injectable({ providedIn: 'root' })
export class BilanService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws/bilan`;

  resume(): Observable<Bilan> {
    return this.http.get<Bilan>(`${this.base}/resume`);
  }

  enregistrer(section: SectionBilan, rubrique: RubriqueBilan): Observable<RubriqueBilan> {
    return this.http.post<RubriqueBilan>(`${this.base}/${section}/enregistrer`, rubrique);
  }

  supprimer(section: SectionBilan, id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${section}/supprimer/${id}`);
  }
}
