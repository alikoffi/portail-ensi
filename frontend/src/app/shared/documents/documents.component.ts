import { Component, HostListener, computed, effect, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { DocumentService } from '@/core/services/document.service';
import { NotificationService } from '@/core/services/notification.service';
import { DocumentJoint } from '@/core/models/document.model';

/**
 * Bloc « pièces jointes » réutilisable : liste, dépôt, aperçu, téléchargement
 * et suppression des documents rattachés à un procès-verbal ou à un évènement.
 */
@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './documents.component.html'
})
export class DocumentsComponent {
  private readonly documentService = inject(DocumentService);
  private readonly notification = inject(NotificationService);
  private readonly sanitizer = inject(DomSanitizer);

  /** Rattachement : l'un des deux, jamais les deux. */
  readonly pvId = input<number | null | undefined>(null);
  readonly evenementId = input<number | null | undefined>(null);

  /** Autorise le dépôt et la suppression. */
  readonly peutGerer = input(false);

  /** Titre du bloc. */
  readonly titre = input('Pièces jointes');

  readonly documents = signal<DocumentJoint[]>([]);
  readonly chargement = signal(false);
  readonly envoiEnCours = signal(false);
  readonly aSupprimer = signal<DocumentJoint | null>(null);

  readonly apercuUrl = signal<SafeResourceUrl | null>(null);
  /** Même URL, non sanitisée : `<img>` attend une SafeUrl, pas une SafeResourceUrl. */
  readonly apercuUrlBrute = signal<string | null>(null);
  readonly apercuDocument = signal<DocumentJoint | null>(null);
  readonly pleinEcran = signal(false);
  private urlObjet: string | null = null;

  /** Une image s'affiche mieux dans un <img> centré que dans une iframe. */
  readonly apercuEstImage = computed(() => this.apercuDocument()?.contentType.startsWith('image/') ?? false);

  readonly vide = computed(() => !this.chargement() && this.documents().length === 0);

  constructor() {
    effect(() => {
      const pv = this.pvId();
      const evenement = this.evenementId();
      if (pv != null || evenement != null) {
        this.charger(pv, evenement);
      } else {
        this.documents.set([]);
      }
    });
  }

  private charger(pvId?: number | null, evenementId?: number | null): void {
    this.chargement.set(true);
    const requete =
      pvId != null
        ? this.documentService.listerParPv(pvId)
        : this.documentService.listerParEvenement(evenementId as number);
    requete.subscribe({
      next: (docs) => {
        this.documents.set(docs);
        this.chargement.set(false);
      },
      error: () => {
        this.documents.set([]);
        this.chargement.set(false);
      }
    });
  }

  private rattachement(): { pvId?: number; evenementId?: number } {
    const pv = this.pvId();
    return pv != null ? { pvId: pv } : { evenementId: this.evenementId() as number };
  }

  private recharger(): void {
    this.charger(this.pvId(), this.evenementId());
  }

  // ---------- Dépôt ----------
  choisirFichier(evenement: Event): void {
    const input = evenement.target as HTMLInputElement;
    const fichier = input.files?.[0];
    if (!fichier) {
      return;
    }
    this.envoyer(fichier);
    input.value = '';
  }

  private envoyer(fichier: File): void {
    this.envoiEnCours.set(true);
    const premier = this.documents().length === 0;
    this.documentService.televerser(fichier, this.rattachement(), { principal: premier }).subscribe({
      next: () => {
        this.envoiEnCours.set(false);
        this.notification.succes('Document ajouté.');
        this.recharger();
      },
      error: (erreur) => {
        this.envoiEnCours.set(false);
        this.notification.erreur(erreur?.error?.message ?? "Le dépôt du document a échoué.");
      }
    });
  }

  // ---------- Aperçu / téléchargement ----------
  estVisualisable(doc: DocumentJoint): boolean {
    return doc.contentType === 'application/pdf' || doc.contentType.startsWith('image/');
  }

  ouvrirApercu(doc: DocumentJoint): void {
    this.documentService.apercu(doc.id).subscribe({
      next: (blob) => {
        this.libererApercu();
        this.urlObjet = URL.createObjectURL(blob);
        // Sur un PDF, le lecteur intégré ouvre la page ajustée à la largeur
        const affichage =
          doc.contentType === 'application/pdf' ? `${this.urlObjet}#zoom=page-width` : this.urlObjet;
        this.apercuUrlBrute.set(this.urlObjet);
        this.apercuUrl.set(this.sanitizer.bypassSecurityTrustResourceUrl(affichage));
        this.apercuDocument.set(doc);
      },
      error: () => this.notification.erreur("L'aperçu du document a échoué.")
    });
  }

  basculerPleinEcran(): void {
    this.pleinEcran.update((v) => !v);
  }

  /** Échap : sortir du plein écran, puis fermer l'aperçu. */
  @HostListener('document:keydown.escape')
  surEchap(): void {
    if (!this.apercuDocument()) {
      return;
    }
    if (this.pleinEcran()) {
      this.pleinEcran.set(false);
    } else {
      this.fermerApercu();
    }
  }

  fermerApercu(): void {
    this.apercuDocument.set(null);
    this.apercuUrl.set(null);
    this.apercuUrlBrute.set(null);
    this.pleinEcran.set(false);
    this.libererApercu();
  }

  private libererApercu(): void {
    if (this.urlObjet) {
      URL.revokeObjectURL(this.urlObjet);
      this.urlObjet = null;
    }
  }

  telecharger(doc: DocumentJoint): void {
    this.documentService.telecharger(doc.id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const lien = document.createElement('a');
        lien.href = url;
        lien.download = doc.nomOriginal;
        lien.click();
        URL.revokeObjectURL(url);
      },
      error: () => this.notification.erreur('Le téléchargement a échoué.')
    });
  }

  // ---------- Suppression ----------
  demanderSuppression(doc: DocumentJoint): void {
    this.aSupprimer.set(doc);
  }

  annulerSuppression(): void {
    this.aSupprimer.set(null);
  }

  confirmerSuppression(): void {
    const doc = this.aSupprimer();
    if (!doc) {
      return;
    }
    this.documentService.supprimer(doc.id).subscribe({
      next: () => {
        this.aSupprimer.set(null);
        this.notification.succes('Document supprimé.');
        this.recharger();
      },
      error: () => {
        this.aSupprimer.set(null);
        this.notification.erreur('La suppression a échoué.');
      }
    });
  }

  // ---------- Affichage ----------
  icone(doc: DocumentJoint): string {
    if (doc.contentType === 'application/pdf') {
      return 'pi-file-pdf';
    }
    if (doc.contentType.startsWith('image/')) {
      return 'pi-image';
    }
    if (doc.contentType.includes('word')) {
      return 'pi-file-word';
    }
    return 'pi-file';
  }

  taille(octets: number): string {
    if (octets < 1024) {
      return `${octets} o`;
    }
    if (octets < 1024 * 1024) {
      return `${Math.round(octets / 1024)} Ko`;
    }
    return `${(octets / 1024 / 1024).toFixed(1)} Mo`;
  }
}
