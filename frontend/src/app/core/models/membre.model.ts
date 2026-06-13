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
}

export interface Recouvrement {
  membres: MembreRecouvrement[];
  totalGeneral: number;
  nombreMembres: number;
  nombreMembresActifs: number;
}
