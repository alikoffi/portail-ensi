import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { AuthService } from '@/core/services/auth.service';
import { AppelCotisationService } from '@/core/services/appel-cotisation.service';
import { NotificationService } from '@/core/services/notification.service';
import { AppelCotisation, LignePaiement } from '@/core/models/appel-cotisation.model';

@Component({
  selector: 'app-cotisations',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableModule],
  templateUrl: './cotisations.component.html'
})
export class CotisationsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(AppelCotisationService);
  private readonly authService = inject(AuthService);
  private readonly notification = inject(NotificationService);

  readonly peutGerer = this.authService.peutGererFinances;

  readonly appels = signal<AppelCotisation[]>([]);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);

  // Modal appel
  readonly modalAppel = signal(false);
  readonly enEditionAppel = signal(false);
  readonly enregistrementAppel = signal(false);
  readonly erreurAppel = signal<string | null>(null);
  readonly appelASupprimer = signal<AppelCotisation | null>(null);
  readonly suppressionEnCours = signal(false);

  // Détail
  readonly appelSelectionne = signal<AppelCotisation | null>(null);
  readonly lignes = signal<LignePaiement[]>([]);
  readonly chargementDetail = signal(false);

  // Modal paiement
  readonly modalPaiement = signal(false);
  readonly ligneCourante = signal<LignePaiement | null>(null);
  readonly enregistrementPaiement = signal(false);

  readonly formAppel = this.fb.nonNullable.group({
    id: this.fb.control<number | null>(null),
    libelle: ['', Validators.required],
    montantAttendu: this.fb.control<number | null>(null, [Validators.required, Validators.min(0.01)]),
    dateButoir: ['', Validators.required]
  });

  readonly formPaiement = this.fb.nonNullable.group({
    montant: this.fb.control<number | null>(null, [Validators.required, Validators.min(0.01)]),
    datePaiement: [''],
    note: ['']
  });

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.service.lister().subscribe({
      next: (a) => {
        this.appels.set(a);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger les appels de cotisation.');
        this.chargement.set(false);
      }
    });
  }

  formater(montant: number | undefined | null): string {
    if (montant === undefined || montant === null) {
      return '0 FCFA';
    }
    return new Intl.NumberFormat('fr-FR').format(montant) + ' FCFA';
  }

  progression(a: AppelCotisation): number {
    const total = (a.montantAttendu ?? 0) * (a.nombrePayeurs ?? 0);
    if (!a.totalCollecte || total <= 0) {
      return 0;
    }
    return Math.min(100, Math.round(((a.totalCollecte ?? 0) / total) * 100));
  }

  // ---------- Appel CRUD ----------
  ouvrirCreationAppel(): void {
    this.enEditionAppel.set(false);
    this.erreurAppel.set(null);
    this.formAppel.reset({ id: null, libelle: '', montantAttendu: null, dateButoir: '' });
    this.modalAppel.set(true);
  }

  ouvrirEditionAppel(a: AppelCotisation): void {
    this.enEditionAppel.set(true);
    this.erreurAppel.set(null);
    this.formAppel.reset({ id: a.id ?? null, libelle: a.libelle, montantAttendu: a.montantAttendu, dateButoir: a.dateButoir });
    this.modalAppel.set(true);
  }

  fermerModalAppel(): void {
    this.modalAppel.set(false);
  }

  enregistrerAppel(): void {
    if (this.formAppel.invalid) {
      this.formAppel.markAllAsTouched();
      return;
    }
    this.enregistrementAppel.set(true);
    this.erreurAppel.set(null);
    const v = this.formAppel.getRawValue();
    const edition = this.enEditionAppel();
    const payload: AppelCotisation = {
      id: v.id ?? undefined,
      libelle: v.libelle,
      montantAttendu: v.montantAttendu as number,
      dateButoir: v.dateButoir
    };
    const req = edition ? this.service.modifier(payload) : this.service.enregistrer(payload);
    req.subscribe({
      next: () => {
        this.enregistrementAppel.set(false);
        this.modalAppel.set(false);
        this.notification.succes(edition ? 'Appel modifié.' : 'Appel créé — les membres ont été notifiés.');
        this.charger();
      },
      error: () => {
        this.enregistrementAppel.set(false);
        this.erreurAppel.set("L'enregistrement a échoué.");
      }
    });
  }

  demanderSuppressionAppel(a: AppelCotisation): void {
    this.appelASupprimer.set(a);
  }

  annulerSuppression(): void {
    this.appelASupprimer.set(null);
  }

  confirmerSuppressionAppel(): void {
    const a = this.appelASupprimer();
    if (!a?.id) {
      return;
    }
    this.suppressionEnCours.set(true);
    this.service.supprimer(a.id).subscribe({
      next: () => {
        this.suppressionEnCours.set(false);
        this.appelASupprimer.set(null);
        this.notification.succes('Appel supprimé.');
        this.charger();
      },
      error: () => {
        this.suppressionEnCours.set(false);
        this.appelASupprimer.set(null);
        this.notification.erreur('La suppression a échoué.');
      }
    });
  }

  // ---------- Détail / paiements ----------
  ouvrirDetail(a: AppelCotisation): void {
    this.appelSelectionne.set(a);
    this.chargerDetail(a.id!);
  }

  fermerDetail(): void {
    this.appelSelectionne.set(null);
    this.lignes.set([]);
  }

  private chargerDetail(id: number): void {
    this.chargementDetail.set(true);
    this.service.detail(id).subscribe({
      next: (d) => {
        this.appelSelectionne.set(d.appel);
        this.lignes.set(d.lignes);
        this.chargementDetail.set(false);
      },
      error: () => this.chargementDetail.set(false)
    });
  }

  ouvrirSaisiePaiement(ligne: LignePaiement): void {
    this.ligneCourante.set(ligne);
    this.formPaiement.reset({
      montant: ligne.montantPaye || ligne.montantAttendu,
      datePaiement: ligne.datePaiement ?? '',
      note: ligne.note ?? ''
    });
    this.modalPaiement.set(true);
  }

  fermerModalPaiement(): void {
    this.modalPaiement.set(false);
  }

  enregistrerPaiement(): void {
    const appel = this.appelSelectionne();
    const ligne = this.ligneCourante();
    if (!appel?.id || !ligne || this.formPaiement.invalid) {
      this.formPaiement.markAllAsTouched();
      return;
    }
    this.enregistrementPaiement.set(true);
    const v = this.formPaiement.getRawValue();
    this.service.enregistrerPaiement({
      appelId: appel.id,
      membreId: ligne.membreId,
      montant: v.montant as number,
      datePaiement: v.datePaiement || null,
      note: v.note || null
    }).subscribe({
      next: () => {
        this.enregistrementPaiement.set(false);
        this.modalPaiement.set(false);
        this.notification.succes('Paiement enregistré.');
        this.chargerDetail(appel.id!);
        this.charger();
      },
      error: () => {
        this.enregistrementPaiement.set(false);
        this.notification.erreur("L'enregistrement a échoué.");
      }
    });
  }

  supprimerPaiement(ligne: LignePaiement): void {
    const appel = this.appelSelectionne();
    if (!appel?.id || !ligne.paiementId) {
      return;
    }
    this.service.supprimerPaiement(appel.id, ligne.membreId).subscribe({
      next: () => {
        this.notification.succes('Paiement supprimé.');
        this.chargerDetail(appel.id!);
        this.charger();
      },
      error: () => this.notification.erreur('La suppression a échoué.')
    });
  }
}
