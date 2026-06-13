export interface EvenementApercu {
  id: number;
  nom: string;
  dateEvent: string;
  heure: string | null;
  type: string | null;
  lieu: string | null;
  description: string | null;
}

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
  dernieresTransactions: TransactionApercu[];
}
