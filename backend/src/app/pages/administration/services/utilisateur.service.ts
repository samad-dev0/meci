import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AppUser {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  fonction?: string;
  service?: string;
  motDePasse?: string;
  actif: boolean;
  superAdmin?: boolean;
  gestionnairePartage?: boolean;
  peutPartagerRapports?: boolean;
  requestedAccessRights?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class UtilisateurService {

  private apiUrl = 'http://localhost:8080/api/utilisateurs';

  constructor(private http: HttpClient) {}

  // Liste des utilisateurs
  getUtilisateurs(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(this.apiUrl);
  }

  // Récupérer un utilisateur
  getUtilisateur(id: number): Observable<AppUser> {
    return this.http.get<AppUser>(`${this.apiUrl}/${id}`);
  }

  getAccessRights(id: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/${id}/access-rights`);
  }

  // Créer
  creerUtilisateur(utilisateur: AppUser): Observable<AppUser> {
    return this.http.post<AppUser>(
      this.apiUrl,
      utilisateur
    );
  }

  // Modifier
  modifierUtilisateur(
    id: number,
    utilisateur: AppUser
  ): Observable<AppUser> {

    return this.http.put<AppUser>(
      `${this.apiUrl}/${id}`,
      utilisateur
    );
  }

  // Supprimer
  supprimerUtilisateur(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}
