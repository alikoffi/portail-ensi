import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Transaction } from '@/core/models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws/transaction`;

  lister(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.base}/lister`);
  }

  enregistrer(transaction: Transaction): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.base}/enregistrer`, transaction);
  }

  modifier(transaction: Transaction): Observable<Transaction> {
    return this.http.put<Transaction>(`${this.base}/modifier`, transaction);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/supprimer/${id}`);
  }
}
