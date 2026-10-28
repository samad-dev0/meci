import { Component, OnInit } from '@angular/core';
import { RapportService } from '../services/rapport.service';
import { Rapport, StatutRapport, TypeRapport } from '../models/rapport.model';

@Component({
  selector: 'ngx-rapport-list',
  templateUrl: './rapport-list.component.html',
  styleUrls: ['./rapport-list.component.scss'],
})
export class RapportListComponent implements OnInit {
  rapports: Rapport[] = [];
  loading = false;
  recherche = '';
  statutFiltre: StatutRapport | '' = '';
  typeFiltre: TypeRapport | '' = '';

  selectedRapport: Rapport | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(private rapportService: RapportService) {}

  ngOnInit(): void {
    this.chargerRapports();
  }

  chargerRapports(): void {
    this.loading = true;
    this.errorMessage = '';
    this.rapportService.getRapports(
      this.recherche ? this.recherche : undefined,
      this.statutFiltre ? this.statutFiltre : undefined,
      this.typeFiltre ? this.typeFiltre : undefined
    ).subscribe({
      next: (data) => {
        this.rapports = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Impossible de charger la liste des rapports. Vérifiez que le serveur backend est démarré.';
        this.loading = false;
      },
    });
  }

  rechercher(): void {
    this.chargerRapports();
  }

  reinitialiser(): void {
    this.recherche = '';
    this.statutFiltre = '';
    this.typeFiltre = '';
    this.chargerRapports();
  }

  voirDetails(rapport: Rapport): void {
    this.selectedRapport = rapport;
  }

  fermerDetails(): void {
    this.selectedRapport = null;
  }

  supprimer(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce rapport ?')) {
      this.rapportService.deleteRapport(id).subscribe({
        next: () => {
          this.successMessage = 'Rapport supprimé avec succès.';
          this.chargerRapports();
          setTimeout(() => (this.successMessage = ''), 4000);
        },
        error: () => {
          this.errorMessage = 'Erreur lors de la suppression du rapport.';
        },
      });
    }
  }

  getBadgeStatus(statut?: StatutRapport): string {
    switch (statut) {
      case 'VALIDE':
        return 'success';
      case 'EN_COURS_VALIDATION':
        return 'info';
      case 'REJETE':
        return 'danger';
      case 'EN_ATTENTE':
      default:
        return 'warning';
    }
  }

  getStatutLibelle(statut?: StatutRapport): string {
    switch (statut) {
      case 'VALIDE':
        return 'Validé';
      case 'EN_COURS_VALIDATION':
        return 'En cours de validation';
      case 'REJETE':
        return 'Rejeté';
      case 'EN_ATTENTE':
      default:
        return 'En attente';
    }
  }
}
