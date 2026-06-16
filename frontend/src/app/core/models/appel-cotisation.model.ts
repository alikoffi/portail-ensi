export interface AppelCotisation {
  id?: number;
  libelle: string;
  montantAttendu: number;
  dateButoir: string;
  dateCreation?: string | null;
  cloture?: boolean;
  totalCollecte?: number;
  nombrePayeurs?: number;
  echeanceDepassee?: boolean;
}

export interface LignePaiement {
  paiementId: number | null;
  membreId: number;
  membreNom: string;
  statutMembre: string;
  montantAttendu: number;
  montantPaye: number;
  reste: number;
  aJour: boolean;
  datePaiement: string | null;
  note: string | null;
}

export interface AppelDetail {
  appel: AppelCotisation;
  lignes: LignePaiement[];
}

export interface PaiementSaisie {
  appelId: number;
  membreId: number;
  montant: number;
  datePaiement?: string | null;
  note?: string | null;
}
