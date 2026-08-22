/** Statut saisi par le bureau. */
export type StatutEvenement = 'PLANIFIE' | 'TERMINE' | 'ANNULE' | 'REPORTE';

/** Etat affiche, calcule par le back a partir du statut et de la date. */
export type EtatEvenement = 'A_VENIR' | 'AUJOURDHUI' | 'A_CLOTURER' | 'TERMINE' | 'ANNULE' | 'REPORTE';

export interface Evenement {
  id?: number;
  nom: string;
  dateEvent: string; // ISO yyyy-MM-dd
  heure?: string | null;
  type?: string | null;
  lieu?: string | null;
  description?: string | null;
  statut?: StatutEvenement | null;
  etat?: EtatEvenement | null;
  pvId?: number | null;
  pvObjet?: string | null;
  nbDocuments?: number;
}

/** Libelle et couleur du badge d'etat, partages par le planning et le tableau de bord. */
export const ETATS_EVENEMENT: Record<EtatEvenement, { libelle: string; classe: string }> = {
  A_VENIR: { libelle: 'À venir', classe: 'bg-emerald-50 text-emerald-700' },
  AUJOURDHUI: { libelle: "Aujourd'hui", classe: 'bg-blue-50 text-blue-700' },
  A_CLOTURER: { libelle: 'À clôturer', classe: 'bg-amber-50 text-amber-700' },
  TERMINE: { libelle: 'Terminé', classe: 'bg-gray-100 text-gray-500' },
  ANNULE: { libelle: 'Annulé', classe: 'bg-red-50 text-red-600' },
  REPORTE: { libelle: 'Reporté', classe: 'bg-violet-50 text-violet-700' }
};

export function etatEvenement(ev: Evenement): { libelle: string; classe: string } {
  return ETATS_EVENEMENT[ev.etat ?? 'A_VENIR'] ?? ETATS_EVENEMENT.A_VENIR;
}
