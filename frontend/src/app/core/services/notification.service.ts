import { Injectable, inject } from '@angular/core';
import { MessageService } from 'primeng/api';

/**
 * Notifications toast (succès / erreur / info) au-dessus de PrimeNG MessageService.
 */
@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly messageService = inject(MessageService);

  succes(detail: string, resume = 'Succès'): void {
    this.messageService.add({ severity: 'success', summary: resume, detail, life: 3000 });
  }

  erreur(detail: string, resume = 'Erreur'): void {
    this.messageService.add({ severity: 'error', summary: resume, detail, life: 4000 });
  }

  info(detail: string, resume = 'Information'): void {
    this.messageService.add({ severity: 'info', summary: resume, detail, life: 3000 });
  }
}
