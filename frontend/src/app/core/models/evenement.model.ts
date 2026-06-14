export interface Evenement {
  id?: number;
  nom: string;
  dateEvent: string; // ISO yyyy-MM-dd
  heure?: string | null;
  type?: string | null;
  lieu?: string | null;
  description?: string | null;
}
