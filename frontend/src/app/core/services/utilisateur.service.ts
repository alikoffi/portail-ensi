import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { RoleUtilisateur, Utilisateur } from '@/core/models/utilisateur.model';

export interface CreationUtilisateur {
  username: string;
  password: string;
  role: RoleUtilisateur;
  label?: string;
  email?: string;
}

export interface ModificationUtilisateur {
  id: number;
  role: RoleUtilisateur;
  label?: string;
  email?: string;
}

export interface ProfilMaj {
  label?: string;
  email?: string;
}

export interface ChangementMotDePasse {
  ancienMotDePasse: string;
  nouveauMotDePasse: string;
}

@Injectable({ providedIn: 'root' })
export class UtilisateurService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/ws/utilisateur`;

  lister(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(`${this.base}/lister`);
  }

  enregistrer(payload: CreationUtilisateur): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(`${this.base}/enregistrer`, payload);
  }

  modifier(payload: ModificationUtilisateur): Observable<Utilisateur> {
    return this.http.put<Utilisateur>(`${this.base}/modifier`, payload);
  }

  basculerActif(id: number): Observable<Utilisateur> {
    return this.http.put<Utilisateur>(`${this.base}/basculer-actif/${id}`, {});
  }

  // ----- Profil de l'utilisateur connecté -----
  moi(): Observable<Utilisateur> {
    return this.http.get<Utilisateur>(`${this.base}/moi`);
  }

  modifierProfil(payload: ProfilMaj): Observable<Utilisateur> {
    return this.http.put<Utilisateur>(`${this.base}/profil`, payload);
  }

  changerMotDePasse(payload: ChangementMotDePasse): Observable<void> {
    return this.http.put<void>(`${this.base}/mot-de-passe`, payload);
  }
}
