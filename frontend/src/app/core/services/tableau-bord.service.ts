import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { TableauBord } from '@/core/models/tableau-bord.model';

@Injectable({ providedIn: 'root' })
export class TableauBordService {
  private readonly http = inject(HttpClient);

  resume(): Observable<TableauBord> {
    return this.http.get<TableauBord>(`${environment.apiUrl}/ws/tableau-de-bord/resume`);
  }
}
