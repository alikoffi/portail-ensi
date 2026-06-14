export type StatutMembre = 'ACTIF' | 'INACTIF';

export interface Membre {
  id?: number;
  nom: string;
  prenoms?: string | null;
  matricule?: string | null;
  email?: string | null;
  telephone?: string | null;
  specialite?: string | null;
  statut: StatutMembre;
  dateAdhesion?: string | null;
}

export interface Cotisation {
  id?: number;
  membreId: number;
  membreNom?: string;
  periode: string;
  montant: number;
  datePaiement: string;
  note?: string | null;
}

export interface MembreRecouvrement {
  id: number;
  nom: string;
  prenoms?: string | null;
  statut: StatutMembre;
  totalCotise: number;
  nombrePaiements: number;
  dernierPaiement?: string | null;
  moisEnRetard: number;
}

/** Ligne aplatie pour le tableau des membres (tri/filtre PrimeNG). */
export interface LigneMembre {
  membre: Membre;
  id: number;
  nomComplet: string;
  matricule: string;
  specialite: string;
  statut: StatutMembre;
  totalCotise: number;
  dernierPaiement: string | null;
  moisEnRetard: number;
}

export interface Recouvrement {
  membres: MembreRecouvrement[];
  totalGeneral: number;
  nombreMembres: number;
  nombreMembresActifs: number;
}

export interface StatistiqueCotisation {
  annee: number;
  anneesDisponibles: number[];
  moisLabels: string[];
  montantsParMois: number[];
  totalAnnee: number;
}
