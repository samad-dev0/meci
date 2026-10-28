import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

export interface Rapport {
  id?: number;
  titre: string;
  dateCreation: Date;
  auteur: string;
  methode: 'importé' | 'direct';
  contenu?: string; 
}

@Injectable({
  providedIn: 'root'
})
export class GestionRapportService {
  private apiUrl = 'http://localhost:8080/api/rapports';

  // Base de données simulée en mémoire pour que l'interface fonctionne
  private mockRapports: Rapport[] = [
    { id: 1, titre: 'Rapport Annuel 2025', dateCreation: new Date('2025-12-01'), auteur: 'Admin 1', methode: 'importé' },
    { id: 2, titre: 'Bilan Mensuel', dateCreation: new Date('2026-01-15'), auteur: 'Admin 2', methode: 'direct' }
  ];
  private nextId = 3;

  constructor(private http: HttpClient) { }

  getRapports(): Observable<Rapport[]> {
    // Simulation d'un appel HTTP réussi
    return of([...this.mockRapports]).pipe(delay(500));
  }

  ajouterRapportDirect(rapport: Rapport): Observable<Rapport> {
    const nouveauRapport = { ...rapport, id: this.nextId++ };
    this.mockRapports.unshift(nouveauRapport); // Ajout au début de la liste
    return of(nouveauRapport).pipe(delay(500));
  }

  ajouterRapportFichier(titre: string, auteur: string, fichier: File): Observable<Rapport> {
    const nouveauRapport: Rapport = {
      id: this.nextId++,
      titre: titre,
      auteur: auteur,
      dateCreation: new Date(),
      methode: 'importé'
    };
    this.mockRapports.unshift(nouveauRapport);
    return of(nouveauRapport).pipe(delay(500));
  }

  supprimerRapport(id: number): Observable<void> {
    this.mockRapports = this.mockRapports.filter(r => r.id !== id);
    return of(undefined).pipe(delay(500));
  }

  telechargerRapport(id: number): Observable<Blob> {
    const rapport = this.mockRapports.find(r => r.id === id);
    const contenu = rapport ? `Contenu de ${rapport.titre}` : 'Rapport non trouvé';
    const blob = new Blob([contenu], { type: 'text/plain' });
    return of(blob).pipe(delay(500));
  }
}
