import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Statistique, TableauBord } from '@/core/models/tableau-bord.model';

@Injectable({ providedIn: 'root' })
export class TableauBordService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws/tableau-de-bord`;

  resume(): Observable<TableauBord> {
    return this.http.get<TableauBord>(`${this.base}/resume`);
  }

  statistiques(): Observable<Statistique> {
    return this.http.get<Statistique>(`${this.base}/statistiques`);
  }
}
