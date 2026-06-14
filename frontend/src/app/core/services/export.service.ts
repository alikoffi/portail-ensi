import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

@Injectable({ providedIn: 'root' })
export class ExportService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  journalPdf(): Observable<Blob> {
    return this.http.get(`${this.api}/ws/transaction/export/pdf`, { responseType: 'blob' });
  }

  bilanPdf(): Observable<Blob> {
    return this.http.get(`${this.api}/ws/bilan/export/pdf`, { responseType: 'blob' });
  }

  pvPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.api}/ws/pv/export/pdf/${id}`, { responseType: 'blob' });
  }

  /** Declenche le telechargement d'un blob cote navigateur. */
  telecharger(blob: Blob, nomFichier: string): void {
    const url = window.URL.createObjectURL(blob);
    const lien = document.createElement('a');
    lien.href = url;
    lien.download = nomFichier;
    lien.click();
    window.URL.revokeObjectURL(url);
  }
}
