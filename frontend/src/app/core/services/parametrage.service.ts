import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { CategorieParametrage, Parametrage } from '@/core/models/parametrage.model';

@Injectable({ providedIn: 'root' })
export class ParametrageService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws/parametrage`;

  lister(categorie: CategorieParametrage): Observable<Parametrage[]> {
    return this.http.get<Parametrage[]>(`${this.base}/${categorie}/lister`);
  }

  listerActifs(categorie: CategorieParametrage): Observable<Parametrage[]> {
    return this.http.get<Parametrage[]>(`${this.base}/${categorie}/actifs`);
  }

  enregistrer(parametrage: Parametrage): Observable<Parametrage> {
    return this.http.post<Parametrage>(`${this.base}/enregistrer`, parametrage);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/supprimer/${id}`);
  }
}
