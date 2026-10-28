import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { Rapport, StatutRapport, TypeRapport } from '../models/rapport.model';

@Injectable({
  providedIn: 'root',
})
export class RapportService {
  private apiUrl = `${environment.apiUrl}/rapports`;

  constructor(private http: HttpClient) {}

  getRapports(recherche?: string, statut?: StatutRapport, typeRapport?: TypeRapport): Observable<Rapport[]> {
    const params = this.creerParametresRecherche(recherche, statut, typeRapport);
    return this.http.get<Rapport[]>(this.apiUrl, { params });
  }

  getRapportsAdmin(recherche?: string, statut?: StatutRapport, typeRapport?: TypeRapport): Observable<Rapport[]> {
    const params = this.creerParametresRecherche(recherche, statut, typeRapport);
    return this.http.get<Rapport[]>(`${environment.apiUrl}/admin/rapports`, { params });
  }

  private creerParametresRecherche(
    recherche?: string,
    statut?: StatutRapport,
    typeRapport?: TypeRapport,
  ): HttpParams {
    let params = new HttpParams();
    if (recherche) {
      params = params.set('recherche', recherche);
    }
    if (statut) {
      params = params.set('statut', statut);
    }
    if (typeRapport) {
      params = params.set('typeRapport', typeRapport);
    }
    return params;
  }

  getMesRapports(): Observable<Rapport[]> {
    return this.http.get<Rapport[]>(`${this.apiUrl}/mes-rapports`);
  }

  getRapportsPartages(): Observable<Rapport[]> {
    return this.http.get<Rapport[]>(`${this.apiUrl}/partages`);
  }

  getRapportById(id: number): Observable<Rapport> {
    return this.http.get<Rapport>(`${this.apiUrl}/${id}`);
  }

  createRapport(rapport: Rapport): Observable<Rapport> {
    return this.http.post<Rapport>(this.apiUrl, rapport);
  }

  updateRapport(id: number, rapport: Rapport): Observable<Rapport> {
    return this.http.put<Rapport>(`${this.apiUrl}/${id}`, rapport);
  }

  updateStatut(id: number, statut: StatutRapport, commentaires?: string): Observable<Rapport> {
    return this.http.patch<Rapport>(`${this.apiUrl}/${id}/statut`, { statut, commentaires });
  }

  updateStatutAdmin(id: number, statut: StatutRapport, commentaires?: string): Observable<Rapport> {
    return this.http.patch<Rapport>(`${environment.apiUrl}/admin/rapports/${id}/statut`, { statut, commentaires });
  }

  deleteRapport(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  deleteRapportAdmin(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/admin/rapports/${id}`);
  }

  telechargerRapport(id: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.apiUrl}/${id}/telecharger`, {
      observe: 'response',
      responseType: 'blob',
    });
  }

  telechargerEtSauvegarder(id: number, nomParDefaut: string): Observable<void> {
    return this.telechargerRapport(id).pipe(
      tap(response => {
        const blob = response.body;
        if (!blob) {
          return;
        }
        const disposition = response.headers.get('content-disposition') || '';
        const utfMatch = /filename\*=UTF-8''([^;]+)/i.exec(disposition);
        const asciiMatch = /filename="?([^";]+)"?/i.exec(disposition);
        let filename = nomParDefaut;
        if (utfMatch && utfMatch[1]) {
          filename = decodeURIComponent(utfMatch[1]);
        } else if (asciiMatch && asciiMatch[1]) {
          filename = asciiMatch[1];
        }
        const url = window.URL.createObjectURL(blob);
        const lien = document.createElement('a');
        lien.href = url;
        lien.download = filename;
        lien.click();
        window.URL.revokeObjectURL(url);
      }),
      map(() => undefined),
    );
  }

  creerRapportAvecFichier(
    titre: string,
    fichier: File,
    typeRapport: TypeRapport,
    contenu?: string,
  ): Observable<Rapport> {
    const formData = new FormData();
    formData.append('titre', titre);
    formData.append('typeRapport', typeRapport);
    if (contenu) {
      formData.append('contenu', contenu);
    }
    formData.append('fichier', fichier, fichier.name);
    return this.http.post<Rapport>(`${this.apiUrl}/fichier`, formData);
  }
}
