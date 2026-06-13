import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '@/core/services/auth.service';
import { PvService } from '@/core/services/pv.service';
import { Pv } from '@/core/models/pv.model';

@Component({
  selector: 'app-proces-verbaux',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './proces-verbaux.component.html'
})
export class ProcesVerbauxComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly pvService = inject(PvService);
  private readonly authService = inject(AuthService);

  readonly estAdmin = this.authService.estAdmin;

  readonly pvs = signal<Pv[]>([]);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);
  readonly recherche = signal('');

  readonly pvSelectionne = signal<Pv | null>(null);

  readonly modalFormulaire = signal(false);
  readonly enEdition = signal(false);
  readonly enregistrement = signal(false);
  readonly erreurFormulaire = signal<string | null>(null);

  readonly aSupprimer = signal<Pv | null>(null);
  readonly suppressionEnCours = signal(false);

  readonly form = this.fb.nonNullable.group({
    id: this.fb.control<number | null>(null),
    objet: ['', Validators.required],
    datePv: ['', Validators.required],
    lieu: [''],
    presents: [''],
    ordreDuJour: [''],
    decisions: [''],
    signataires: ['']
  });

  readonly filtres = computed(() => {
    const q = this.recherche().trim().toLowerCase();
    return this.pvs().filter(
      (pv) =>
        !q ||
        pv.objet.toLowerCase().includes(q) ||
        (pv.lieu ?? '').toLowerCase().includes(q) ||
        (pv.decisions ?? '').toLowerCase().includes(q)
    );
  });

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.pvService.lister().subscribe({
      next: (pvs) => {
        this.pvs.set(pvs);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger les procès-verbaux.');
        this.chargement.set(false);
      }
    });
  }

  // ---------- Détail ----------
  ouvrirDetail(pv: Pv): void {
    this.pvSelectionne.set(pv);
  }

  fermerDetail(): void {
    this.pvSelectionne.set(null);
  }

  // ---------- Formulaire ----------
  ouvrirCreation(): void {
    this.enEdition.set(false);
    this.erreurFormulaire.set(null);
    this.form.reset({ id: null, objet: '', datePv: '', lieu: '', presents: '', ordreDuJour: '', decisions: '', signataires: '' });
    this.modalFormulaire.set(true);
  }

  ouvrirEdition(pv: Pv): void {
    this.enEdition.set(true);
    this.erreurFormulaire.set(null);
    this.pvSelectionne.set(null);
    this.form.reset({
      id: pv.id ?? null,
      objet: pv.objet,
      datePv: pv.datePv,
      lieu: pv.lieu ?? '',
      presents: pv.presents ?? '',
      ordreDuJour: pv.ordreDuJour ?? '',
      decisions: pv.decisions ?? '',
      signataires: pv.signataires ?? ''
    });
    this.modalFormulaire.set(true);
  }

  fermerFormulaire(): void {
    this.modalFormulaire.set(false);
  }

  enregistrer(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enregistrement.set(true);
    this.erreurFormulaire.set(null);

    const v = this.form.getRawValue();
    const payload: Pv = {
      id: v.id ?? undefined,
      objet: v.objet,
      datePv: v.datePv,
      lieu: v.lieu || null,
      presents: v.presents || null,
      ordreDuJour: v.ordreDuJour || null,
      decisions: v.decisions || null,
      signataires: v.signataires || null
    };

    const requete = this.enEdition() ? this.pvService.modifier(payload) : this.pvService.enregistrer(payload);
    requete.subscribe({
      next: () => {
        this.enregistrement.set(false);
        this.modalFormulaire.set(false);
        this.charger();
      },
      error: () => {
        this.enregistrement.set(false);
        this.erreurFormulaire.set("L'enregistrement a échoué.");
      }
    });
  }

  // ---------- Suppression ----------
  demanderSuppression(pv: Pv): void {
    this.pvSelectionne.set(null);
    this.aSupprimer.set(pv);
  }

  annulerSuppression(): void {
    this.aSupprimer.set(null);
  }

  confirmerSuppression(): void {
    const pv = this.aSupprimer();
    if (!pv?.id) {
      return;
    }
    this.suppressionEnCours.set(true);
    this.pvService.supprimer(pv.id).subscribe({
      next: () => {
        this.suppressionEnCours.set(false);
        this.aSupprimer.set(null);
        this.charger();
      },
      error: () => {
        this.suppressionEnCours.set(false);
        this.aSupprimer.set(null);
        this.erreur.set('La suppression a échoué.');
      }
    });
  }
}
