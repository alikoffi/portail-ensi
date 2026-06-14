export type RoleUtilisateur = 'ADMIN' | 'TRESORIER' | 'SECRETAIRE' | 'VIEWER';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  role: RoleUtilisateur;
  label: string;
}

export interface Utilisateur {
  id: number;
  username: string;
  role: RoleUtilisateur;
  label: string;
  email?: string | null;
  actif: boolean;
  derniereConnexion?: string | null;
}
