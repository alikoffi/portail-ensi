export interface Pv {
  id?: number;
  objet: string;
  datePv: string; // ISO yyyy-MM-dd
  lieu?: string | null;
  presents?: string | null;
  ordreDuJour?: string | null;
  decisions?: string | null;
  signataires?: string | null;
}
