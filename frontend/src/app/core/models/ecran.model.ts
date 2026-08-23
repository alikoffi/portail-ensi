/** Code identifiant un écran de l'application, côté back comme côté menu. */
export type CodeEcran =
  | 'TABLEAU_DE_BORD'
  | 'PLANNING'
  | 'FINANCES'
  | 'MEMBRES'
  | 'COTISATIONS'
  | 'PROCES_VERBAUX'
  | 'COMPTES'
  | 'PARAMETRAGE'
  | 'ECRANS';

export interface Ecran {
  id: number;
  code: CodeEcran;
  libelle: string;
  visible: boolean;
  /** Un écran verrouillé ne peut pas être masqué. */
  verrouille: boolean;
  ordre: number;
}
