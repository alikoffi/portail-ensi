export type TypeTransaction = 'RECETTE' | 'DEPENSE';

export interface Transaction {
  id?: number;
  libelle: string;
  type: TypeTransaction;
  montant: number;
  dateTx: string; // ISO yyyy-MM-dd
  categorie?: string | null;
  note?: string | null;
}
