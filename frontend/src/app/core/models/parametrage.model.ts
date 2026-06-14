export type CategorieParametrage = 'TYPE_EVENEMENT' | 'CATEGORIE_TRANSACTION' | 'SPECIALITE';

export interface Parametrage {
  id?: number;
  categorie: CategorieParametrage;
  libelle: string;
  ordre: number;
  actif: boolean;
}
