import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AutorisationDTO, AppUser } from '../../rapports/models/rapport.model';

export interface DelegationRequest {
  beneficiaireIds: number[];
  beneficiaireNoms?: string[];
  proprietaireId: number;
  proprietaireNom?: string;
  tousRapports: boolean;
  rapportIds: number[];
}

@Injectable({
  providedIn: 'root',
})
export class AdminAutorisationService {
  private apiUrl = `${environment.apiUrl}/admin/autorisations`;
  private userUrl = `${environment.apiUrl}/admin/utilisateurs`;

  constructor(private http: HttpClient) {}

  getUsers(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(this.userUrl);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.userUrl}/${id}`);
  }

  getAutorisations(): Observable<AutorisationDTO[]> {
    return this.http.get<AutorisationDTO[]>(this.apiUrl);
  }

  getAutorisationsParCouple(beneficiaireId: number, proprietaireId: number): Observable<any[]> {
    const params = new HttpParams()
      .set('beneficiaireId', beneficiaireId.toString())
      .set('proprietaireId', proprietaireId.toString());
    return this.http.get<any[]>(`${this.apiUrl}/delegation`, { params });
  }

  sauvegarderDelegation(request: DelegationRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/delegation`, request);
  }
}
