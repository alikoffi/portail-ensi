import { AfterViewInit, Component, ElementRef, OnInit, computed, inject, signal, viewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Chart, registerables } from 'chart.js';
import { TableModule } from 'primeng/table';
import { DatePickerModule } from 'primeng/datepicker';
import { AuthService } from '@/core/services/auth.service';
import { MembreService } from '@/core/services/membre.service';
import { AppelCotisationService } from '@/core/services/appel-cotisation.service';
import { NotificationService } from '@/core/services/notification.service';
import { ParametrageService } from '@/core/services/parametrage.service';
import { LigneMembre, Membre, Recouvrement, StatistiqueCotisation, StatutMembre } from '@/core/models/membre.model';

Chart.register(...registerables);

@Component({
  selector: 'app-membres',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, TableModule, DatePickerModule],
  templateUrl: './membres.component.html'
})
export class MembresComponent implements OnInit, AfterViewInit {
  private readonly fb = inject(FormBuilder);
  private readonly membreService = inject(MembreService);
  private readonly appelService = inject(AppelCotisationService);
  private readonly authService = inject(AuthService);
  private readonly notification = inject(NotificationService);
  private readonly parametrageService = inject(ParametrageService);

  readonly peutGererMembres = this.authService.peutGererMembres;
  readonly specialites = signal<string[]>([]);

  readonly membres = signal<Membre[]>([]);
  readonly recouvrement = signal<Recouvrement | null>(null);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);

  // Évolution des cotisations
  readonly statsCotisations = signal<StatistiqueCotisation | null>(null);
  readonly anneeSelectionnee = signal<number | null>(null);
  readonly canvasCotisations = viewChild<ElementRef<HTMLCanvasElement>>('canvasCotisations');
  private chartCotisations?: Chart;
  private vueInitialisee = false;

  // Drawer détail
  readonly membreSelectionne = signal<Membre | null>(null);

  // Modal membre
  readonly modalMembre = signal(false);
  readonly enEditionMembre = signal(false);
  readonly enregistrementMembre = signal(false);
  readonly erreurMembre = signal<string | null>(null);

  // Suppression
  readonly membreASupprimer = signal<Membre | null>(null);
  readonly suppressionEnCours = signal(false);

  readonly formMembre = this.fb.nonNullable.group({
    id: this.fb.control<number | null>(null),
    nom: ['', Validators.required],
    prenoms: [''],
    matricule: [''],
    email: ['', Validators.email],
    telephone: [''],
    specialite: [''],
    statut: ['ACTIF' as StatutMembre, Validators.required],
    dateAdhesion: ['']
  });

  readonly totalParMembre = computed(() => {
    const map = new Map<number, number>();
    for (const r of this.recouvrement()?.membres ?? []) {
      map.set(r.id, r.totalCotise);
    }
    return map;
  });

  readonly dernierParMembre = computed(() => {
    const map = new Map<number, string | null>();
    for (const r of this.recouvrement()?.membres ?? []) {
      map.set(r.id, r.dernierPaiement ?? null);
    }
    return map;
  });

  readonly retardParMembre = computed(() => {
    const map = new Map<number, { appels: number; reste: number }>();
    for (const r of this.recouvrement()?.membres ?? []) {
      map.set(r.id, { appels: r.appelsEnRetard ?? 0, reste: r.resteAPayer ?? 0 });
    }
    return map;
  });

  /** Lignes aplaties pour le p-table (tri + filtres par colonne). */
  readonly lignesMembres = computed<LigneMembre[]>(() => {
    const totals = this.totalParMembre();
    const derniers = this.dernierParMembre();
    const retards = this.retardParMembre();
    return this.membres().map((m) => ({
      membre: m,
      id: m.id!,
      nomComplet: `${m.nom} ${m.prenoms ?? ''}`.trim(),
      matricule: m.matricule ?? '',
      specialite: m.specialite ?? '',
      statut: m.statut,
      totalCotise: totals.get(m.id!) ?? 0,
      dernierPaiement: derniers.get(m.id!) ?? null,
      appelsEnRetard: retards.get(m.id!)?.appels ?? 0,
      resteAPayer: retards.get(m.id!)?.reste ?? 0
    }));
  });

  // Sélecteur d'année (DatePicker PrimeNG, vue année)
  readonly anneeDate = signal<Date | null>(null);

  ngOnInit(): void {
    this.charger();
    this.chargerStatistiques();
    this.parametrageService.listerActifs('SPECIALITE').subscribe({
      next: (vs) => this.specialites.set(vs.map((v) => v.libelle))
    });
  }

  ngAfterViewInit(): void {
    this.vueInitialisee = true;
    this.dessinerGraphique();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    forkJoin({ membres: this.membreService.lister(), recouvrement: this.membreService.recouvrement() }).subscribe({
      next: ({ membres, recouvrement }) => {
        this.membres.set(membres);
        this.recouvrement.set(recouvrement);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger les membres.');
        this.chargement.set(false);
      }
    });
  }

  chargerStatistiques(annee?: number): void {
    this.appelService.statistiques(annee).subscribe({
      next: (s) => {
        this.statsCotisations.set(s);
        this.anneeSelectionnee.set(s.annee);
        this.anneeDate.set(new Date(s.annee, 0, 1));
        this.dessinerGraphique();
      },
      error: () => {}
    });
  }

  onAnneeSelect(date: Date): void {
    if (date) {
      this.chargerStatistiques(date.getFullYear());
    }
  }

  private dessinerGraphique(): void {
    const stats = this.statsCotisations();
    const el = this.canvasCotisations()?.nativeElement;
    if (!this.vueInitialisee || !stats || !el) {
      return;
    }
    const ctx = el.getContext('2d');
    const degrade = ctx ? ctx.createLinearGradient(0, 0, 0, 240) : undefined;
    if (degrade) {
      degrade.addColorStop(0, 'rgba(11, 93, 59, 0.35)');
      degrade.addColorStop(1, 'rgba(11, 93, 59, 0.02)');
    }

    this.chartCotisations?.destroy();
    this.chartCotisations = new Chart(el, {
      type: 'line',
      data: {
        labels: stats.moisLabels,
        datasets: [
          {
            label: 'Cotisations (FCFA)',
            data: stats.montantsParMois,
            borderColor: '#0b5d3b',
            backgroundColor: degrade ?? 'rgba(11, 93, 59, 0.15)',
            fill: true,
            tension: 0.4,
            borderWidth: 2,
            pointBackgroundColor: '#0b5d3b',
            pointRadius: 3,
            pointHoverRadius: 5
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (c) => `${new Intl.NumberFormat('fr-FR').format(Number(c.parsed.y))} FCFA`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { callback: (v) => new Intl.NumberFormat('fr-FR').format(Number(v)) },
            grid: { color: 'rgba(0,0,0,0.05)' }
          },
          x: { grid: { display: false } }
        }
      }
    });
  }

  formater(montant: number | undefined): string {
    if (montant === undefined || montant === null) {
      return '0 FCFA';
    }
    return new Intl.NumberFormat('fr-FR').format(montant) + ' FCFA';
  }

  totalMembre(id: number | undefined): number {
    return id ? this.totalParMembre().get(id) ?? 0 : 0;
  }

  retardMembre(id: number | undefined): { appels: number; reste: number } {
    return id ? this.retardParMembre().get(id) ?? { appels: 0, reste: 0 } : { appels: 0, reste: 0 };
  }

  initiales(m: Membre): string {
    return ((m.nom?.charAt(0) ?? '') + (m.prenoms?.charAt(0) ?? '')).toUpperCase();
  }

  // ---------- Drawer ----------
  ouvrirDetail(m: Membre): void {
    this.membreSelectionne.set(m);
  }

  fermerDetail(): void {
    this.membreSelectionne.set(null);
  }

  // ---------- Membre form ----------
  ouvrirCreationMembre(): void {
    this.enEditionMembre.set(false);
    this.erreurMembre.set(null);
    this.formMembre.reset({ id: null, nom: '', prenoms: '', matricule: '', email: '', telephone: '', specialite: '', statut: 'ACTIF', dateAdhesion: '' });
    this.modalMembre.set(true);
  }

  ouvrirEditionMembre(m: Membre): void {
    this.enEditionMembre.set(true);
    this.erreurMembre.set(null);
    this.membreSelectionne.set(null);
    this.formMembre.reset({
      id: m.id ?? null,
      nom: m.nom,
      prenoms: m.prenoms ?? '',
      matricule: m.matricule ?? '',
      email: m.email ?? '',
      telephone: m.telephone ?? '',
      specialite: m.specialite ?? '',
      statut: m.statut,
      dateAdhesion: m.dateAdhesion ?? ''
    });
    this.modalMembre.set(true);
  }

  fermerModalMembre(): void {
    this.modalMembre.set(false);
  }

  enregistrerMembre(): void {
    if (this.formMembre.invalid) {
      this.formMembre.markAllAsTouched();
      return;
    }
    this.enregistrementMembre.set(true);
    this.erreurMembre.set(null);
    const v = this.formMembre.getRawValue();
    const payload: Membre = {
      id: v.id ?? undefined,
      nom: v.nom,
      prenoms: v.prenoms || null,
      matricule: v.matricule || null,
      email: v.email || null,
      telephone: v.telephone || null,
      specialite: v.specialite || null,
      statut: v.statut,
      dateAdhesion: v.dateAdhesion || null
    };
    const edition = this.enEditionMembre();
    const req = edition ? this.membreService.modifier(payload) : this.membreService.enregistrer(payload);
    req.subscribe({
      next: () => {
        this.enregistrementMembre.set(false);
        this.modalMembre.set(false);
        this.notification.succes(edition ? 'Membre modifié.' : 'Membre ajouté.');
        this.charger();
      },
      error: () => {
        this.enregistrementMembre.set(false);
        this.erreurMembre.set("L'enregistrement a échoué.");
      }
    });
  }

  // ---------- Suppression ----------
  demanderSuppressionMembre(m: Membre): void {
    this.membreSelectionne.set(null);
    this.membreASupprimer.set(m);
  }

  annulerSuppression(): void {
    this.membreASupprimer.set(null);
  }

  confirmerSuppression(): void {
    const membre = this.membreASupprimer();
    if (!membre?.id) {
      return;
    }
    this.suppressionEnCours.set(true);
    this.membreService.supprimer(membre.id).subscribe({
      next: () => {
        this.suppressionEnCours.set(false);
        this.membreASupprimer.set(null);
        this.notification.succes('Membre supprimé.');
        this.charger();
      },
      error: () => {
        this.suppressionEnCours.set(false);
        this.membreASupprimer.set(null);
        this.notification.erreur('La suppression a échoué.');
      }
    });
  }
}
