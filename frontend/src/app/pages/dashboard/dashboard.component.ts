import { AfterViewInit, Component, ElementRef, OnInit, computed, inject, signal, viewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { AuthService } from '@/core/services/auth.service';
import { TableauBordService } from '@/core/services/tableau-bord.service';
import { RappelService } from '@/core/services/rappel.service';
import { NotificationService } from '@/core/services/notification.service';
import { Statistique, TableauBord } from '@/core/models/tableau-bord.model';

Chart.register(...registerables);

// Palette diversifiee pour distinguer les categories (le vert ENSI reste en tete).
const COULEURS_CATEGORIES = [
  '#0b5d3b', // vert ENSI
  '#f59e0b', // ambre
  '#3b82f6', // bleu
  '#ef4444', // rouge
  '#8b5cf6', // violet
  '#14b8a6', // turquoise
  '#ec4899', // rose
  '#64748b'  // ardoise
];

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit, AfterViewInit {
  private readonly authService = inject(AuthService);
  private readonly tableauBordService = inject(TableauBordService);
  private readonly rappelService = inject(RappelService);
  private readonly notification = inject(NotificationService);

  readonly utilisateur = this.authService.user;
  readonly estAdmin = this.authService.estAdmin;
  readonly envoiRappels = signal(false);
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

  envoyerRappels(): void {
    this.envoiRappels.set(true);
    this.rappelService.envoyer().subscribe({
      next: (r) => {
        this.envoiRappels.set(false);
        if (r.nombreDestinataires === 0) {
          this.notification.info('Aucun destinataire avec email à notifier.');
        } else if (r.nombreEvenements === 0 && r.nombreMembresEnRetard === 0) {
          this.notification.info('Rien à signaler : aucun rappel envoyé.');
        } else {
          this.notification.succes(
            `Rappels envoyés à ${r.nombreDestinataires} destinataire(s) — ${r.nombreEvenements} évènement(s), ${r.nombreMembresEnRetard} cotisation(s) en retard.`
          );
        }
      },
      error: () => {
        this.envoiRappels.set(false);
        this.notification.erreur("L'envoi des rappels a échoué.");
      }
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
      const formatFcfa = (valeur: number) => new Intl.NumberFormat('fr-FR').format(valeur) + ' FCFA';
      this.chartDepenses = new Chart(elDepenses, {
        type: 'doughnut',
        data: {
          labels: stats.categoriesDepenses,
          datasets: [{ data: stats.montantsDepenses, backgroundColor: COULEURS_CATEGORIES, borderWidth: 0 }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'right',
              labels: {
                boxWidth: 12,
                font: { size: 11 },
                // Legende = libelle + montant + unite FCFA
                generateLabels: (chart) => {
                  const labels = (chart.data.labels ?? []) as string[];
                  const valeurs = (chart.data.datasets[0].data ?? []) as number[];
                  const couleurs = chart.data.datasets[0].backgroundColor as string[];
                  return labels.map((label, i) => ({
                    text: `${label} — ${formatFcfa(valeurs[i])}`,
                    fillStyle: couleurs[i],
                    strokeStyle: couleurs[i],
                    lineWidth: 0,
                    index: i
                  }));
                }
              }
            },
            tooltip: {
              callbacks: {
                label: (ctx) => `${ctx.label} : ${formatFcfa(Number(ctx.parsed))}`
              }
            }
          }
        }
      });
    }
  }
}
