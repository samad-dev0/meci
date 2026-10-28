import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { Rapport } from '../../rapports/models/rapport.model';

interface AutorisationRapportBackend {
  tousRapports: boolean;
  rapportId: number;
  rapportTitre?: string;
}

export interface AutorisationUtilisateurRequest {
  beneficiaireId: number;
  rapportIds: number[];
}

export interface AutorisationRequest {
  proprietaireId: number;
  autorisations: AutorisationUtilisateurRequest[];
}

export interface PartageOwnerRequest {
  proprietaireId: number;
  autorisations: AutorisationUtilisateurRequest[];
}

export interface PartageRapport {
  beneficiaireId: number;
  beneficiaireNom: string;
  proprietaireId: number;
  proprietaireNom: string;
  tousRapports: boolean;
  rapportId: number | null;
  rapportTitre: string | null;
}

export interface RetraitPartageRequest {
  proprietaireId: number;
  beneficiaireId: number;
  rapportId: number;
}

export interface PartageUtilisateur {
  beneficiaireId: number;
  beneficiaireNom: string;
  proprietaireId: number;
  proprietaireNom: string;
  tousRapports: boolean;
  rapportId: number | null;
  rapportTitre: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class AutorisationRapportService {
  private readonly apiUrl = `${environment.apiUrl}/autorisations`;

  constructor(private readonly http: HttpClient) {}

  getGestionnaires(): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/gestionnaires`);
  }

  enregistrerGestionnaires(utilisateurIds: number[]): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/gestionnaires`, utilisateurIds);
  }

  getUtilisateursAutorisesAuPartage(): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/partageurs`);
  }

  enregistrerUtilisateursAutorisesAuPartage(utilisateurIds: number[]): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/partageurs`, utilisateurIds);
  }

  peutPartager(): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/peut-partager`);
  }

  getMesPartages(): Observable<PartageUtilisateur[]> {
    return this.http.get<PartageUtilisateur[]>(`${this.apiUrl}/mes-partages`);
  }

  accorderMesPartages(partages: PartageOwnerRequest[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/mes-partages`, partages);
  }

  transfererSuperAdmin(utilisateurId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/super-admin`, utilisateurId);
  }

  getPartages(): Observable<PartageRapport[]> {
    return this.http.get<PartageRapport[]>(`${this.apiUrl}/partages`);
  }

  getRapportsDuProprietaire(proprietaireId: number): Observable<Rapport[]> {
    return this.http.get<Rapport[]>(`${this.apiUrl}/proprietaires/${proprietaireId}/rapports`);
  }

  enregistrerPartages(partages: PartageOwnerRequest[]): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/partages`, partages);
  }

  accorderPartages(partages: PartageOwnerRequest[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/partages/accorder`, partages);
  }

  retirerPartage(proprietaireId: number, beneficiaireId: number, rapportId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/partages`, {
      params: {
        proprietaireId: proprietaireId.toString(),
        beneficiaireId: beneficiaireId.toString(),
        rapportId: rapportId.toString(),
      },
    });
  }

  retirerPartages(partages: RetraitPartageRequest[]): Observable<void> {
    return this.http.request<void>('delete', `${this.apiUrl}/partages/selection`, {
      body: partages,
    });
  }

  getBeneficiairesAutorises(proprietaireId: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/beneficiaires`, {
      params: { proprietaireId: proprietaireId.toString() },
    });
  }

  /**
   * Récupère toutes les autorisations existantes
   */
  /**
   * Récupère les IDs des rapports autorisés pour un utilisateur
   */
  getRapportIdsAutorises(beneficiaireId: number, proprietaireId: number): Observable<number[]> {
    return this.http.get<AutorisationRapportBackend[]>(`${this.apiUrl}/delegation`, {
      params: {
        beneficiaireId: beneficiaireId.toString(),
        proprietaireId: proprietaireId.toString(),
      },
    }).pipe(
      map(autorisations => autorisations
        .filter(autorisation => !autorisation.tousRapports && autorisation.rapportId != null)
        .map(autorisation => autorisation.rapportId))
    );
  }

  sauvegarderAutorisations(request: AutorisationRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/delegations`, request);
  }
}
