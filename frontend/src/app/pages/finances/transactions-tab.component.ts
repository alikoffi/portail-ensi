import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '@/core/services/auth.service';
import { TransactionService } from '@/core/services/transaction.service';
import { ExportService } from '@/core/services/export.service';
import { Transaction, TypeTransaction } from '@/core/models/transaction.model';

@Component({
  selector: 'app-transactions-tab',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transactions-tab.component.html'
})
export class TransactionsTabComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly transactionService = inject(TransactionService);
  private readonly exportService = inject(ExportService);
  private readonly authService = inject(AuthService);

  readonly estAdmin = this.authService.estAdmin;
  readonly exportEnCours = signal(false);

  readonly transactions = signal<Transaction[]>([]);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);

  readonly recherche = signal('');
  readonly typeFiltre = signal<'' | TypeTransaction>('');

  readonly modalOuvert = signal(false);
  readonly enEdition = signal(false);
  readonly enregistrement = signal(false);
  readonly erreurFormulaire = signal<string | null>(null);

  readonly aSupprimer = signal<Transaction | null>(null);
  readonly suppressionEnCours = signal(false);

  readonly form = this.fb.nonNullable.group({
    id: this.fb.control<number | null>(null),
    libelle: ['', Validators.required],
    type: ['RECETTE' as TypeTransaction, Validators.required],
    montant: this.fb.control<number | null>(null, [Validators.required, Validators.min(0.01)]),
    dateTx: ['', Validators.required],
    categorie: [''],
    note: ['']
  });

  readonly filtrees = computed(() => {
    const q = this.recherche().trim().toLowerCase();
    const type = this.typeFiltre();
    return this.transactions().filter((t) => {
      const okType = !type || t.type === type;
      const okTexte =
        !q ||
        t.libelle.toLowerCase().includes(q) ||
        (t.categorie ?? '').toLowerCase().includes(q);
      return okType && okTexte;
    });
  });

  readonly totalRecettes = computed(() =>
    this.transactions().filter((t) => t.type === 'RECETTE').reduce((s, t) => s + t.montant, 0)
  );
  readonly totalDepenses = computed(() =>
    this.transactions().filter((t) => t.type === 'DEPENSE').reduce((s, t) => s + t.montant, 0)
  );
  readonly solde = computed(() => this.totalRecettes() - this.totalDepenses());

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.transactionService.lister().subscribe({
      next: (txs) => {
        this.transactions.set(txs);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger les transactions.');
        this.chargement.set(false);
      }
    });
  }

  formater(montant: number): string {
    return new Intl.NumberFormat('fr-FR').format(montant) + ' FCFA';
  }

  exporterPdf(): void {
    this.exportEnCours.set(true);
    this.exportService.journalPdf().subscribe({
      next: (blob) => {
        this.exportService.telecharger(blob, 'journal-transactions.pdf');
        this.exportEnCours.set(false);
      },
      error: () => {
        this.exportEnCours.set(false);
        this.erreur.set("L'export PDF a échoué.");
      }
    });
  }

  ouvrirCreation(): void {
    this.enEdition.set(false);
    this.erreurFormulaire.set(null);
    this.form.reset({ id: null, libelle: '', type: 'RECETTE', montant: null, dateTx: '', categorie: '', note: '' });
    this.modalOuvert.set(true);
  }

  ouvrirEdition(t: Transaction): void {
    this.enEdition.set(true);
    this.erreurFormulaire.set(null);
    this.form.reset({
      id: t.id ?? null,
      libelle: t.libelle,
      type: t.type,
      montant: t.montant,
      dateTx: t.dateTx,
      categorie: t.categorie ?? '',
      note: t.note ?? ''
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

    const v = this.form.getRawValue();
    const payload: Transaction = {
      id: v.id ?? undefined,
      libelle: v.libelle,
      type: v.type,
      montant: v.montant as number,
      dateTx: v.dateTx,
      categorie: v.categorie || null,
      note: v.note || null
    };

    const requete = this.enEdition()
      ? this.transactionService.modifier(payload)
      : this.transactionService.enregistrer(payload);

    requete.subscribe({
      next: () => {
        this.enregistrement.set(false);
        this.modalOuvert.set(false);
        this.charger();
      },
      error: () => {
        this.enregistrement.set(false);
        this.erreurFormulaire.set("L'enregistrement a échoué.");
      }
    });
  }

  demanderSuppression(t: Transaction): void {
    this.aSupprimer.set(t);
  }

  annulerSuppression(): void {
    this.aSupprimer.set(null);
  }

  confirmerSuppression(): void {
    const t = this.aSupprimer();
    if (!t?.id) {
      return;
    }
    this.suppressionEnCours.set(true);
    this.transactionService.supprimer(t.id).subscribe({
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
