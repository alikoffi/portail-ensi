import { AfterViewInit, Component, ElementRef, OnInit, computed, inject, signal, viewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Chart, registerables } from 'chart.js';
import { AuthService } from '@/core/services/auth.service';
import { MembreService } from '@/core/services/membre.service';
import { Cotisation, Membre, Recouvrement, StatistiqueCotisation, StatutMembre } from '@/core/models/membre.model';

Chart.register(...registerables);

@Component({
  selector: 'app-membres',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './membres.component.html'
})
export class MembresComponent implements OnInit, AfterViewInit {
  private readonly fb = inject(FormBuilder);
  private readonly membreService = inject(MembreService);
  private readonly authService = inject(AuthService);

  readonly estAdmin = this.authService.estAdmin;

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

  readonly recherche = signal('');
  readonly statutFiltre = signal<'' | StatutMembre>('');

  // Drawer détail
  readonly membreSelectionne = signal<Membre | null>(null);
  readonly cotisations = signal<Cotisation[]>([]);
  readonly chargementCotisations = signal(false);

  // Modal membre
  readonly modalMembre = signal(false);
  readonly enEditionMembre = signal(false);
  readonly enregistrementMembre = signal(false);
  readonly erreurMembre = signal<string | null>(null);

  // Modal cotisation
  readonly modalCotisation = signal(false);
  readonly enregistrementCotisation = signal(false);
  readonly erreurCotisation = signal<string | null>(null);

  // Suppressions
  readonly membreASupprimer = signal<Membre | null>(null);
  readonly cotisationASupprimer = signal<Cotisation | null>(null);
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

  readonly formCotisation = this.fb.nonNullable.group({
    periode: ['', Validators.required],
    montant: this.fb.control<number | null>(null, [Validators.required, Validators.min(0.01)]),
    datePaiement: ['', Validators.required],
    note: ['']
  });

  readonly totalParMembre = computed(() => {
    const map = new Map<number, number>();
    for (const r of this.recouvrement()?.membres ?? []) {
      map.set(r.id, r.totalCotise);
    }
    return map;
  });

  readonly membresFiltres = computed(() => {
    const q = this.recherche().trim().toLowerCase();
    const statut = this.statutFiltre();
    return this.membres().filter((m) => {
      const okStatut = !statut || m.statut === statut;
      const okTexte =
        !q ||
        m.nom.toLowerCase().includes(q) ||
        (m.prenoms ?? '').toLowerCase().includes(q) ||
        (m.matricule ?? '').toLowerCase().includes(q) ||
        (m.specialite ?? '').toLowerCase().includes(q);
      return okStatut && okTexte;
    });
  });

  ngOnInit(): void {
    this.charger();
    this.chargerStatistiques();
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
    this.membreService.statistiquesCotisations(annee).subscribe({
      next: (s) => {
        this.statsCotisations.set(s);
        this.anneeSelectionnee.set(s.annee);
        this.dessinerGraphique();
      },
      error: () => {}
    });
  }

  changerAnnee(annee: number): void {
    this.chargerStatistiques(annee);
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

  initiales(m: Membre): string {
    return ((m.nom?.charAt(0) ?? '') + (m.prenoms?.charAt(0) ?? '')).toUpperCase();
  }

  // ---------- Drawer ----------
  ouvrirDetail(m: Membre): void {
    this.membreSelectionne.set(m);
    this.chargerCotisations(m.id!);
  }

  fermerDetail(): void {
    this.membreSelectionne.set(null);
    this.cotisations.set([]);
  }

  private chargerCotisations(membreId: number): void {
    this.chargementCotisations.set(true);
    this.membreService.cotisationsParMembre(membreId).subscribe({
      next: (cs) => {
        this.cotisations.set(cs);
        this.chargementCotisations.set(false);
      },
      error: () => this.chargementCotisations.set(false)
    });
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
    const req = this.enEditionMembre() ? this.membreService.modifier(payload) : this.membreService.enregistrer(payload);
    req.subscribe({
      next: () => {
        this.enregistrementMembre.set(false);
        this.modalMembre.set(false);
        this.charger();
      },
      error: () => {
        this.enregistrementMembre.set(false);
        this.erreurMembre.set("L'enregistrement a échoué.");
      }
    });
  }

  // ---------- Cotisation form ----------
  ouvrirAjoutCotisation(): void {
    this.erreurCotisation.set(null);
    this.formCotisation.reset({ periode: '', montant: null, datePaiement: '', note: '' });
    this.modalCotisation.set(true);
  }

  fermerModalCotisation(): void {
    this.modalCotisation.set(false);
  }

  enregistrerCotisation(): void {
    const membre = this.membreSelectionne();
    if (!membre?.id || this.formCotisation.invalid) {
      this.formCotisation.markAllAsTouched();
      return;
    }
    this.enregistrementCotisation.set(true);
    this.erreurCotisation.set(null);
    const v = this.formCotisation.getRawValue();
    const payload: Cotisation = {
      membreId: membre.id,
      periode: v.periode,
      montant: v.montant as number,
      datePaiement: v.datePaiement,
      note: v.note || null
    };
    this.membreService.enregistrerCotisation(payload).subscribe({
      next: () => {
        this.enregistrementCotisation.set(false);
        this.modalCotisation.set(false);
        this.chargerCotisations(membre.id!);
        this.rafraichirRecouvrement();
      },
      error: () => {
        this.enregistrementCotisation.set(false);
        this.erreurCotisation.set("L'enregistrement a échoué.");
      }
    });
  }

  private rafraichirRecouvrement(): void {
    this.membreService.recouvrement().subscribe({ next: (r) => this.recouvrement.set(r) });
  }

  // ---------- Suppressions ----------
  demanderSuppressionMembre(m: Membre): void {
    this.membreSelectionne.set(null);
    this.membreASupprimer.set(m);
  }

  demanderSuppressionCotisation(c: Cotisation): void {
    this.cotisationASupprimer.set(c);
  }

  annulerSuppression(): void {
    this.membreASupprimer.set(null);
    this.cotisationASupprimer.set(null);
  }

  confirmerSuppression(): void {
    const membre = this.membreASupprimer();
    const cotisation = this.cotisationASupprimer();
    if (membre?.id) {
      this.suppressionEnCours.set(true);
      this.membreService.supprimer(membre.id).subscribe({
        next: () => {
          this.suppressionEnCours.set(false);
          this.membreASupprimer.set(null);
          this.charger();
        },
        error: () => {
          this.suppressionEnCours.set(false);
          this.membreASupprimer.set(null);
          this.erreur.set('La suppression a échoué.');
        }
      });
    } else if (cotisation?.id) {
      this.suppressionEnCours.set(true);
      this.membreService.supprimerCotisation(cotisation.id).subscribe({
        next: () => {
          this.suppressionEnCours.set(false);
          this.cotisationASupprimer.set(null);
          this.chargerCotisations(cotisation.membreId);
          this.rafraichirRecouvrement();
        },
        error: () => {
          this.suppressionEnCours.set(false);
          this.cotisationASupprimer.set(null);
        }
      });
    }
  }
}
