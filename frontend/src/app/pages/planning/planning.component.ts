import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '@/core/services/auth.service';
import { EvenementService } from '@/core/services/evenement.service';
import { NotificationService } from '@/core/services/notification.service';
import { ParametrageService } from '@/core/services/parametrage.service';
import { Evenement } from '@/core/models/evenement.model';

@Component({
  selector: 'app-planning',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './planning.component.html'
})
export class PlanningComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly evenementService = inject(EvenementService);
  private readonly authService = inject(AuthService);
  private readonly notification = inject(NotificationService);
  private readonly parametrageService = inject(ParametrageService);

  readonly peutGerer = this.authService.peutGererPlanning;
  readonly typesEvenement = signal<string[]>([]);

  readonly evenements = signal<Evenement[]>([]);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);

  // Filtres
  readonly recherche = signal('');
  readonly typeFiltre = signal<string>('');

  // Modal formulaire
  readonly modalOuvert = signal(false);
  readonly enEdition = signal(false);
  readonly enregistrement = signal(false);
  readonly erreurFormulaire = signal<string | null>(null);

  // Modal suppression
  readonly evenementASupprimer = signal<Evenement | null>(null);
  readonly suppressionEnCours = signal(false);

  readonly form = this.fb.nonNullable.group({
    id: this.fb.control<number | null>(null),
    nom: ['', Validators.required],
    dateEvent: ['', Validators.required],
    heure: [''],
    type: [''],
    lieu: [''],
    description: ['']
  });

  readonly typesDisponibles = computed(() => {
    const types = new Set<string>();
    for (const ev of this.evenements()) {
      if (ev.type) {
        types.add(ev.type);
      }
    }
    return Array.from(types).sort();
  });

  readonly evenementsFiltres = computed(() => {
    const q = this.recherche().trim().toLowerCase();
    const type = this.typeFiltre();
    return this.evenements().filter((ev) => {
      const correspondType = !type || ev.type === type;
      const correspondTexte =
        !q ||
        ev.nom.toLowerCase().includes(q) ||
        (ev.lieu ?? '').toLowerCase().includes(q) ||
        (ev.description ?? '').toLowerCase().includes(q);
      return correspondType && correspondTexte;
    });
  });

  ngOnInit(): void {
    this.charger();
    this.parametrageService.listerActifs('TYPE_EVENEMENT').subscribe({
      next: (vs) => this.typesEvenement.set(vs.map((v) => v.libelle))
    });
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.evenementService.lister().subscribe({
      next: (evs) => {
        this.evenements.set(evs);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger les évènements.');
        this.chargement.set(false);
      }
    });
  }

  estPasse(dateEvent: string): boolean {
    return new Date(dateEvent) < new Date(new Date().toDateString());
  }

  // ---------- Formulaire ----------
  ouvrirCreation(): void {
    this.enEdition.set(false);
    this.erreurFormulaire.set(null);
    this.form.reset({ id: null, nom: '', dateEvent: '', heure: '', type: '', lieu: '', description: '' });
    this.modalOuvert.set(true);
  }

  ouvrirEdition(ev: Evenement): void {
    this.enEdition.set(true);
    this.erreurFormulaire.set(null);
    this.form.reset({
      id: ev.id ?? null,
      nom: ev.nom,
      dateEvent: ev.dateEvent,
      heure: ev.heure ?? '',
      type: ev.type ?? '',
      lieu: ev.lieu ?? '',
      description: ev.description ?? ''
    });
    this.modalOuvert.set(true);
  }

  fermerModal(): void {
    this.modalOuvert.set(false);
  }

  enregistrer(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enregistrement.set(true);
    this.erreurFormulaire.set(null);

    const valeur = this.form.getRawValue();
    const payload: Evenement = {
      id: valeur.id ?? undefined,
      nom: valeur.nom,
      dateEvent: valeur.dateEvent,
      heure: valeur.heure || null,
      type: valeur.type || null,
      lieu: valeur.lieu || null,
      description: valeur.description || null
    };

    const requete = this.enEdition()
      ? this.evenementService.modifier(payload)
      : this.evenementService.enregistrer(payload);

    const edition = this.enEdition();
    requete.subscribe({
      next: () => {
        this.enregistrement.set(false);
        this.modalOuvert.set(false);
        this.notification.succes(edition ? 'Évènement modifié.' : 'Évènement créé.');
        this.charger();
      },
      error: () => {
        this.enregistrement.set(false);
        this.erreurFormulaire.set("L'enregistrement a échoué. Vérifiez vos droits et les champs.");
      }
    });
  }

  // ---------- Suppression ----------
  demanderSuppression(ev: Evenement): void {
    this.evenementASupprimer.set(ev);
  }

  annulerSuppression(): void {
    this.evenementASupprimer.set(null);
  }

  confirmerSuppression(): void {
    const ev = this.evenementASupprimer();
    if (!ev?.id) {
      return;
    }
    this.suppressionEnCours.set(true);
    this.evenementService.supprimer(ev.id).subscribe({
      next: () => {
        this.suppressionEnCours.set(false);
        this.evenementASupprimer.set(null);
        this.notification.succes('Évènement supprimé.');
        this.charger();
      },
      error: () => {
        this.suppressionEnCours.set(false);
        this.evenementASupprimer.set(null);
        this.notification.erreur('La suppression a échoué.');
      }
    });
  }
}
