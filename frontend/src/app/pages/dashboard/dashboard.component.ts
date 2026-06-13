import { AfterViewInit, Component, ElementRef, OnInit, computed, inject, signal, viewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { AuthService } from '@/core/services/auth.service';
import { TableauBordService } from '@/core/services/tableau-bord.service';
import { Statistique, TableauBord } from '@/core/models/tableau-bord.model';

Chart.register(...registerables);

const COULEURS_ENSI = ['#0b5d3b', '#1f7d56', '#3f9e74', '#6dba96', '#9fd3b9', '#c9e7d7'];

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit, AfterViewInit {
  private readonly authService = inject(AuthService);
  private readonly tableauBordService = inject(TableauBordService);

  readonly utilisateur = this.authService.user;
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);
  readonly donnees = signal<TableauBord | null>(null);
  readonly stats = signal<Statistique | null>(null);

  readonly canvasSolde = viewChild<ElementRef<HTMLCanvasElement>>('canvasSolde');
  readonly canvasDepenses = viewChild<ElementRef<HTMLCanvasElement>>('canvasDepenses');

  private chartSolde?: Chart;
  private chartDepenses?: Chart;
  private vueInitialisee = false;

  readonly indicateurs = computed(() => {
    const d = this.donnees();
    return [
      { libelle: 'Solde actuel', valeur: this.formaterMontant(d?.solde), icone: 'pi-wallet', accent: 'bg-ensi-50 text-ensi-700' },
      { libelle: 'Recettes totales', valeur: this.formaterMontant(d?.recettesTotales), icone: 'pi-arrow-down-left', accent: 'bg-emerald-50 text-emerald-700' },
      { libelle: 'Dépenses totales', valeur: this.formaterMontant(d?.depensesTotales), icone: 'pi-arrow-up-right', accent: 'bg-rose-50 text-rose-700' },
      { libelle: 'Évènements ce mois', valeur: d ? String(d.evenementsCeMois) : '—', icone: 'pi-calendar', accent: 'bg-amber-50 text-amber-700' }
    ];
  });

  ngOnInit(): void {
    this.charger();
  }

  ngAfterViewInit(): void {
    this.vueInitialisee = true;
    this.dessinerGraphiques();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.tableauBordService.resume().subscribe({
      next: (d) => {
        this.donnees.set(d);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger le tableau de bord.');
        this.chargement.set(false);
      }
    });
    this.tableauBordService.statistiques().subscribe({
      next: (s) => {
        this.stats.set(s);
        this.dessinerGraphiques();
      },
      error: () => {}
    });
  }

  formaterMontant(valeur: number | undefined): string {
    if (valeur === undefined || valeur === null) {
      return '—';
    }
    return new Intl.NumberFormat('fr-FR').format(valeur) + ' FCFA';
  }

  private dessinerGraphiques(): void {
    const stats = this.stats();
    if (!this.vueInitialisee || !stats) {
      return;
    }

    const elSolde = this.canvasSolde()?.nativeElement;
    if (elSolde) {
      this.chartSolde?.destroy();
      this.chartSolde = new Chart(elSolde, {
        type: 'line',
        data: {
          labels: stats.moisLabels,
          datasets: [
            {
              label: 'Solde cumulé (FCFA)',
              data: stats.soldeCumule,
              borderColor: '#0b5d3b',
              backgroundColor: 'rgba(11, 93, 59, 0.08)',
              fill: true,
              tension: 0.3,
              pointBackgroundColor: '#0b5d3b'
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: { y: { ticks: { callback: (v) => new Intl.NumberFormat('fr-FR').format(Number(v)) } } }
        }
      });
    }

    const elDepenses = this.canvasDepenses()?.nativeElement;
    if (elDepenses) {
      this.chartDepenses?.destroy();
      this.chartDepenses = new Chart(elDepenses, {
        type: 'doughnut',
        data: {
          labels: stats.categoriesDepenses,
          datasets: [{ data: stats.montantsDepenses, backgroundColor: COULEURS_ENSI, borderWidth: 0 }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'right', labels: { boxWidth: 12, font: { size: 11 } } } }
        }
      });
    }
  }
}
