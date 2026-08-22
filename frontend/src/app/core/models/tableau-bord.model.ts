import { Evenement } from '@/core/models/evenement.model';

/** Un evenement affiche sur le tableau de bord : meme forme que dans le planning. */
export type EvenementApercu = Evenement & { id: number };

export interface TransactionApercu {
  id: number;
  libelle: string;
  type: 'RECETTE' | 'DEPENSE';
  montant: number;
  dateTx: string;
  categorie: string | null;
  note: string | null;
}

export interface TableauBord {
  solde: number;
  recettesTotales: number;
  depensesTotales: number;
  evenementsCeMois: number;
  prochainsEvenements: EvenementApercu[];
  derniersEvenements: EvenementApercu[];
  dernieresTransactions: TransactionApercu[];
}

export interface Statistique {
  moisLabels: string[];
  soldeCumule: number[];
  categoriesDepenses: string[];
  montantsDepenses: number[];
}
