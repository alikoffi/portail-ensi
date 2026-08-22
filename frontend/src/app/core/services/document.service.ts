import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { DocumentJoint } from '@/core/models/document.model';

@Injectable({ providedIn: 'root' })
export class DocumentService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws/document`;

  listerParPv(pvId: number): Observable<DocumentJoint[]> {
    return this.http.get<DocumentJoint[]>(`${this.base}/lister/pv/${pvId}`);
  }

  listerParEvenement(evenementId: number): Observable<DocumentJoint[]> {
    return this.http.get<DocumentJoint[]>(`${this.base}/lister/evenement/${evenementId}`);
  }

  televerser(
    fichier: File,
    rattachement: { pvId?: number; evenementId?: number },
    options: { libelle?: string; principal?: boolean } = {}
  ): Observable<DocumentJoint> {
    const donnees = new FormData();
    donnees.append('fichier', fichier);
    if (rattachement.pvId != null) {
      donnees.append('pvId', String(rattachement.pvId));
    }
    if (rattachement.evenementId != null) {
      donnees.append('evenementId', String(rattachement.evenementId));
    }
    if (options.libelle) {
      donnees.append('libelle', options.libelle);
    }
    donnees.append('principal', String(options.principal ?? false));
    return this.http.post<DocumentJoint>(`${this.base}/televerser`, donnees);
  }

  /** Contenu pour telechargement (Content-Disposition: attachment). */
  telecharger(id: number): Observable<Blob> {
    return this.http.get(`${this.base}/telecharger/${id}`, { responseType: 'blob' });
  }

  /** Contenu pour affichage dans le navigateur (apercu PDF ou image). */
  apercu(id: number): Observable<Blob> {
    return this.http.get(`${this.base}/apercu/${id}`, { responseType: 'blob' });
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/supprimer/${id}`);
  }
}
