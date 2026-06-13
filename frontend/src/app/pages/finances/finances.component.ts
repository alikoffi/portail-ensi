import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransactionsTabComponent } from '@/pages/finances/transactions-tab.component';
import { BilanTabComponent } from '@/pages/finances/bilan-tab.component';

type Onglet = 'transactions' | 'bilan';

@Component({
  selector: 'app-finances',
  standalone: true,
  imports: [CommonModule, TransactionsTabComponent, BilanTabComponent],
  template: `
    <div class="max-w-6xl mx-auto space-y-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">États financiers</h1>
        <p class="mt-1 text-sm text-gray-500">Journal des transactions et bilan de la promotion.</p>
      </div>

      <!-- Onglets -->
      <div class="flex items-center gap-1 p-1 bg-gray-100 rounded-xl w-fit">
        <button
          (click)="onglet.set('transactions')"
          class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          [class.bg-white]="onglet() === 'transactions'"
          [class.text-ensi-700]="onglet() === 'transactions'"
          [class.shadow-sm]="onglet() === 'transactions'"
          [class.text-gray-500]="onglet() !== 'transactions'"
        >
          <i class="pi pi-list mr-1.5"></i> Transactions
        </button>
        <button
          (click)="onglet.set('bilan')"
          class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          [class.bg-white]="onglet() === 'bilan'"
          [class.text-ensi-700]="onglet() === 'bilan'"
          [class.shadow-sm]="onglet() === 'bilan'"
          [class.text-gray-500]="onglet() !== 'bilan'"
        >
          <i class="pi pi-chart-pie mr-1.5"></i> Bilan
        </button>
      </div>

      @if (onglet() === 'transactions') {
        <app-transactions-tab />
      } @else {
        <app-bilan-tab />
      }
    </div>
  `
})
export class FinancesComponent {
  readonly onglet = signal<Onglet>('transactions');
}
