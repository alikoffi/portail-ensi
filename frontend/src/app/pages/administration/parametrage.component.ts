import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ParametrageService } from '@/core/services/parametrage.service';
import { NotificationService } from '@/core/services/notification.service';
import { CategorieParametrage, Parametrage } from '@/core/models/parametrage.model';

const TITRES: Record<CategorieParametrage, { titre: string; sous: string; singulier: string }> = {
  TYPE_EVENEMENT: { titre: "Types d'évènement", sous: 'Valeurs proposées pour le type des évènements du planning.', singulier: 'type' },
  CATEGORIE_TRANSACTION: { titre: 'Catégories de transaction', sous: 'Valeurs proposées pour la catégorie des transactions.', singulier: 'catégorie' },
  SPECIALITE: { titre: 'Spécialités', sous: 'Spécialités proposées pour les membres de la promotion.', singulier: 'spécialité' }
};

@Component({
  selector: 'app-parametrage',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableModule],
  templateUrl: './parametrage.component.html'
})
export class ParametrageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly parametrageService = inject(ParametrageService);
  private readonly notification = inject(NotificationService);

  readonly categorie = signal<CategorieParametrage>('TYPE_EVENEMENT');

  readonly valeurs = signal<Parametrage[]>([]);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);

  readonly modalOuvert = signal(false);
  readonly enEdition = signal(false);
  readonly enregistrement = signal(false);
  readonly erreurFormulaire = signal<string | null>(null);
  readonly aSupprimer = signal<Parametrage | null>(null);
  readonly suppressionEnCours = signal(false);

  readonly form = this.fb.nonNullable.group({
    id: this.fb.control<number | null>(null),
    libelle: ['', Validators.required],
    ordre: [0, Validators.required],
    actif: [true]
  });

  get meta() {
    return TITRES[this.categorie()] ?? { titre: 'Paramétrage', sous: '', singulier: 'valeur' };
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((p) => {
      const cat = (p.get('categorie') as CategorieParametrage) ?? 'TYPE_EVENEMENT';
      this.categorie.set(cat);
      this.charger();
    });
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.parametrageService.lister(this.categorie()).subscribe({
      next: (vs) => {
        this.valeurs.set(vs);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger les valeurs.');
        this.chargement.set(false);
      }
    });
  }

  ouvrirCreation(): void {
    this.enEdition.set(false);
    this.erreurFormulaire.set(null);
    this.form.reset({ id: null, libelle: '', ordre: this.valeurs().length + 1, actif: true });
    this.modalOuvert.set(true);
  }

  ouvrirEdition(p: Parametrage): void {
    this.enEdition.set(true);
    this.erreurFormulaire.set(null);
    this.form.reset({ id: p.id ?? null, libelle: p.libelle, ordre: p.ordre, actif: p.actif });
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
    const v = this.form.getRawValue();
    const edition = this.enEdition();
    const payload: Parametrage = {
      id: v.id ?? undefined,
      categorie: this.categorie(),
      libelle: v.libelle,
      ordre: v.ordre,
      actif: v.actif
    };
    this.parametrageService.enregistrer(payload).subscribe({
      next: () => {
        this.enregistrement.set(false);
        this.modalOuvert.set(false);
        this.notification.succes(edition ? 'Valeur modifiée.' : 'Valeur ajoutée.');
        this.charger();
      },
      error: (err) => {
        this.enregistrement.set(false);
        this.erreurFormulaire.set(err?.status === 409 ? 'Cette valeur existe déjà.' : "L'enregistrement a échoué.");
      }
    });
  }

  basculerActif(p: Parametrage): void {
    this.parametrageService.enregistrer({ ...p, actif: !p.actif }).subscribe({
      next: () => {
        this.notification.succes(!p.actif ? 'Valeur activée.' : 'Valeur désactivée.');
        this.charger();
      },
      error: () => this.notification.erreur('La mise à jour a échoué.')
    });
  }

  demanderSuppression(p: Parametrage): void {
    this.aSupprimer.set(p);
  }

  annulerSuppression(): void {
    this.aSupprimer.set(null);
  }

  confirmerSuppression(): void {
    const p = this.aSupprimer();
    if (!p?.id) {
      return;
    }
    this.suppressionEnCours.set(true);
    this.parametrageService.supprimer(p.id).subscribe({
      next: () => {
        this.suppressionEnCours.set(false);
        this.aSupprimer.set(null);
        this.notification.succes('Valeur supprimée.');
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
