import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '@/core/services/auth.service';
import { PvService } from '@/core/services/pv.service';
import { EvenementService } from '@/core/services/evenement.service';
import { ExportService } from '@/core/services/export.service';
import { NotificationService } from '@/core/services/notification.service';
import { Pv } from '@/core/models/pv.model';
import { Evenement } from '@/core/models/evenement.model';
import { DocumentsComponent } from '@/shared/documents/documents.component';

@Component({
  selector: 'app-proces-verbaux',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DocumentsComponent],
  templateUrl: './proces-verbaux.component.html'
})
export class ProcesVerbauxComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly pvService = inject(PvService);
  private readonly evenementService = inject(EvenementService);
  private readonly route = inject(ActivatedRoute);
  private readonly exportService = inject(ExportService);
  private readonly authService = inject(AuthService);
  private readonly notification = inject(NotificationService);

  readonly peutGerer = this.authService.peutGererPv;
  readonly exportEnCours = signal(false);

  readonly pvs = signal<Pv[]>([]);
  readonly evenements = signal<Evenement[]>([]);

  /** Identifiant du PV ouvert dans le formulaire (filtre des évènements rattachables). */
  private readonly pvEnCours = signal<number | null>(null);
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
    signataires: [''],
    evenementId: this.fb.control<number | null>(null)
  });

  /** Évènements rattachables : ceux sans PV, plus celui déjà lié au PV en cours. */
  readonly evenementsDisponibles = computed(() => {
    const pvCourant = this.pvEnCours();
    return this.evenements().filter((ev) => !ev.pvId || ev.pvId === pvCourant);
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
    this.evenementService.lister().subscribe({ next: (evs) => this.evenements.set(evs) });

    // arrivée depuis le planning : « Créer le PV » d'un évènement
    const evenementId = Number(this.route.snapshot.queryParamMap.get('evenementId'));
    if (evenementId) {
      this.ouvrirCreation(evenementId);
    }
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

  exporterPdf(pv: Pv): void {
    if (!pv.id) {
      return;
    }
    this.exportEnCours.set(true);
    this.exportService.pvPdf(pv.id).subscribe({
      next: (blob) => {
        this.exportService.telecharger(blob, `proces-verbal-${pv.id}.pdf`);
        this.exportEnCours.set(false);
        this.notification.succes('Export PDF généré.');
      },
      error: () => {
        this.exportEnCours.set(false);
        this.notification.erreur("L'export PDF a échoué.");
      }
    });
  }

  // ---------- Formulaire ----------
  ouvrirCreation(evenementId?: number): void {
    this.enEdition.set(false);
    this.erreurFormulaire.set(null);
    this.pvEnCours.set(null);
    this.form.reset({
      id: null, objet: '', datePv: '', lieu: '', presents: '', ordreDuJour: '', decisions: '', signataires: '',
      evenementId: evenementId ?? null
    });
    if (evenementId) {
      this.preremplirDepuisEvenement(evenementId);
    }
    this.modalFormulaire.set(true);
  }

  /** Reprend l'objet, la date et le lieu de l'évènement rattaché. */
  private preremplirDepuisEvenement(evenementId: number): void {
    const ev = this.evenements().find((e) => e.id === evenementId);
    if (ev) {
      this.appliquerEvenement(ev);
      return;
    }
    this.evenementService.lister().subscribe({
      next: (evs) => {
        this.evenements.set(evs);
        const trouve = evs.find((e) => e.id === evenementId);
        if (trouve) {
          this.appliquerEvenement(trouve);
        }
      }
    });
  }

  private appliquerEvenement(ev: Evenement): void {
    this.form.patchValue({
      objet: this.form.controls.objet.value || ev.nom,
      datePv: this.form.controls.datePv.value || ev.dateEvent,
      lieu: this.form.controls.lieu.value || (ev.lieu ?? '')
    });
  }

  /** Sélection d'un évènement dans le formulaire : pré-remplit les champs vides. */
  changerEvenement(valeur: string): void {
    const id = valeur ? Number(valeur) : null;
    this.form.controls.evenementId.setValue(id);
    if (id) {
      this.preremplirDepuisEvenement(id);
    }
  }

  ouvrirEdition(pv: Pv): void {
    this.enEdition.set(true);
    this.erreurFormulaire.set(null);
    this.pvSelectionne.set(null);
    this.pvEnCours.set(pv.id ?? null);
    this.form.reset({
      id: pv.id ?? null,
      objet: pv.objet,
      datePv: pv.datePv,
      lieu: pv.lieu ?? '',
      presents: pv.presents ?? '',
      ordreDuJour: pv.ordreDuJour ?? '',
      decisions: pv.decisions ?? '',
      signataires: pv.signataires ?? '',
      evenementId: pv.evenementId ?? null
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
      signataires: v.signataires || null,
      evenementId: v.evenementId
    };

    const edition = this.enEdition();
    const requete = edition ? this.pvService.modifier(payload) : this.pvService.enregistrer(payload);
    requete.subscribe({
      next: (enregistre) => {
        this.enregistrement.set(false);
        this.modalFormulaire.set(false);
        this.notification.succes(edition ? 'Procès-verbal modifié.' : 'Procès-verbal créé.');
        this.charger();
        this.evenementService.lister().subscribe({ next: (evs) => this.evenements.set(evs) });
        if (!edition) {
          // le PV a désormais un identifiant : on ouvre le détail pour joindre les documents
          this.pvSelectionne.set(enregistre);
        }
      },
      error: (erreur) => {
        this.enregistrement.set(false);
        this.erreurFormulaire.set(erreur?.error?.message ?? "L'enregistrement a échoué.");
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
        this.notification.succes('Procès-verbal supprimé.');
        this.charger();
      },
      error: () => {
        this.suppressionEnCours.set(false);
        this.aSupprimer.set(null);
        this.notification.erreur('La suppression a échoué.');
      }
    });
  }
}
