import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AccessRightsService } from './access-rights.service';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  nom: string;
  prenom: string;
  email: string;
  superAdmin?: boolean;
  gestionnairePartage?: boolean;
  peutPartagerRapports?: boolean;
}

export interface UtilisateurConnecte {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  superAdmin?: boolean;
  gestionnairePartage?: boolean;
  peutPartagerRapports?: boolean;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';

  private tokenKey = 'meci_token';
  private userKey = 'meci_user';

  constructor(
    private http: HttpClient,
    private accessRights: AccessRightsService,
  ) {}

  login(donnees: LoginRequest): Observable<LoginResponse> {

    console.log(
      'Tentative de connexion vers :',
      `${this.apiUrl}/login`
    );

    console.log('Données envoyées :', {
      email: donnees.email,
      password: '********'
    });

    return this.http.post<LoginResponse>(
      `${this.apiUrl}/login`,
      donnees
    ).pipe(
      tap((response) => {

        console.log(
          'Réponse du backend :',
          response
        );

        const utilisateurConnecte: UtilisateurConnecte = {
          id: response.userId,
          nom: response.nom,
          prenom: response.prenom,
          email: response.email,
          superAdmin: !!response.superAdmin,
          gestionnairePartage: !!response.gestionnairePartage,
          peutPartagerRapports: !!response.peutPartagerRapports,
        };

        localStorage.setItem(
          this.tokenKey,
          response.token
        );

        localStorage.setItem(
          this.userKey,
          JSON.stringify(utilisateurConnecte)
        );
        this.accessRights.reset();
      })
    );
  }

  demanderReinitialisation(
    email: string
  ): Observable<any> {

    const donnees: ForgotPasswordRequest = {
      email: email
    };

    return this.http.post(
      `${this.apiUrl}/forgot-password`,
      donnees
    );
  }

  reinitialiserMotDePasse(
    token: string,
    password: string
  ): Observable<any> {

    const donnees = {
      token: token,
      password: password
    };

    console.log('Données envoyées au backend :', {
      token: token,
      password: '********'
    });

    return this.http.post(
      `${this.apiUrl}/reset-password`,
      donnees
    );
  }

  logout(): void {

    localStorage.removeItem(
      this.tokenKey
    );

    localStorage.removeItem(
      this.userKey
    );
    this.accessRights.reset();
  }

  getToken(): string | null {

    return localStorage.getItem(
      this.tokenKey
    );
  }

  getUtilisateurConnecte(): UtilisateurConnecte | null {

    const utilisateur =
      localStorage.getItem(
        this.userKey
      );

    if (!utilisateur) {
      return null;
    }

    return JSON.parse(utilisateur) as UtilisateurConnecte;
  }

  getNomComplet(): string {
    const utilisateur = this.getUtilisateurConnecte();

    if (!utilisateur) {
      return '';
    }

    const prenom = utilisateur.prenom || '';
    const nom = utilisateur.nom || '';

    return `${prenom} ${nom}`.trim();
  }

  estConnecte(): boolean {

    return !!this.getToken();
  }
}
