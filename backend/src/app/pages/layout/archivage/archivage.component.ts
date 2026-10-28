import { Component, OnInit } from '@angular/core';

export interface CourrierDossier {
  id: string;
  objet: string;
  expediteur: string;
  origine: 'Upload' | 'Scan' | 'E-mail';
  statut: 'Reçu' | 'Transmis au DG' | 'En traitement' | 'Soumis au DG' | 'Réponse en attente' | 'Traité';
  priorite: 'Faible' | 'Normale' | 'Urgente';
  dateReception: string;
  responsable: string;
  service: string;
  delai: string;
  traitement: string;
  score: number;
  details: Array<{
    label: string;
    date: string;
    description: string;
    etat: 'termine' | 'actif' | 'attente';
  }>;
}

@Component({
  selector: 'ngx-archivage',
  templateUrl: './archivage.component.html',
  styleUrls: ['./archivage.component.scss'],
})
export class ArchivageComponent implements OnInit {
  dossiers: CourrierDossier[] = [
    {
      id: 'D-2026-018',
      objet: 'Demande de validation du budget 2026',
      expediteur: 'Direction Financière',
      origine: 'Upload',
      statut: 'En traitement',
      priorite: 'Urgente',
      dateReception: '2026-09-17',
      responsable: 'Madame Diop',
      service: 'Finance',
      delai: '2 jours',
      traitement: 'Analyse budgétaire et relecture des pièces',
      score: 82,
      details: [
        { label: 'Réception secrétariat', date: '17 Sept', description: 'Courrier reçu et enregistré', etat: 'termine' },
        { label: 'Validation DG', date: '18 Sept', description: 'Transmission à l’autorité de décision', etat: 'actif' },
        { label: 'Traitement responsable', date: '19 Sept', description: 'Analyse et recommandations', etat: 'attente' },
        { label: 'Réponse finale', date: '20 Sept', description: 'Retour au secrétariat', etat: 'attente' },
      ],
    },
    {
      id: 'D-2026-019',
      objet: 'Contrat fournisseur - Maintenance IT',
      expediteur: 'Société TechNova',
      origine: 'E-mail',
      statut: 'Transmis au DG',
      priorite: 'Normale',
      dateReception: '2026-09-18',
      responsable: 'Mr. Konaté',
      service: 'Achats',
      delai: '4 jours',
      traitement: 'Vérification des clauses contractuelles',
      score: 68,
      details: [
        { label: 'Réception secrétariat', date: '18 Sept', description: 'Envoi du message reçu', etat: 'termine' },
        { label: 'Validation DG', date: '19 Sept', description: 'Transmission au responsable', etat: 'termine' },
        { label: 'Traitement responsable', date: '20 Sept', description: 'Examen du contrat', etat: 'actif' },
        { label: 'Réponse finale', date: '21 Sept', description: 'Envoi officiel', etat: 'attente' },
      ],
    },
    {
      id: 'D-2026-021',
      objet: 'Demande d’intervention RH',
      expediteur: 'Service Personnel',
      origine: 'Scan',
      statut: 'Réponse en attente',
      priorite: 'Faible',
      dateReception: '2026-09-19',
      responsable: 'Mme Kouassi',
      service: 'Ressources Humaines',
      delai: '5 jours',
      traitement: 'Révision des besoins et validation interne',
      score: 76,
      details: [
        { label: 'Réception secrétariat', date: '19 Sept', description: 'Scan validé', etat: 'termine' },
        { label: 'Validation DG', date: '19 Sept', description: 'Approuvé pour traitement', etat: 'termine' },
        { label: 'Traitement responsable', date: '20 Sept', description: 'Synthèse du dossier', etat: 'termine' },
        { label: 'Réponse finale', date: '22 Sept', description: 'Préparation de la réponse', etat: 'actif' },
      ],
    },
    {
      id: 'D-2026-022',
      objet: 'Correspondance urgente - Ministère',
      expediteur: 'Cabinet du Ministre',
      origine: 'Upload',
      statut: 'Traité',
      priorite: 'Urgente',
      dateReception: '2026-09-20',
      responsable: 'Mr. Traoré',
      service: 'Direction générale',
      delai: '1 jour',
      traitement: 'Réponse officielle préparée et expédiée',
      score: 94,
      details: [
        { label: 'Réception secrétariat', date: '20 Sept', description: 'Courrier enregistré', etat: 'termine' },
        { label: 'Validation DG', date: '20 Sept', description: 'Direction générale validée', etat: 'termine' },
        { label: 'Traitement responsable', date: '21 Sept', description: 'Traitement final terminé', etat: 'termine' },
        { label: 'Réponse finale', date: '22 Sept', description: 'Réponse envoyée', etat: 'termine' },
      ],
    },
  ];

  selectedDossier: CourrierDossier = this.dossiers[0];
  statusFilter = 'Tous';

  readonly stats = [
    { label: 'Total dossiers', value: 0, color: '#3366ff' },
    { label: 'En attente', value: 0, color: '#ffaa00' },
    { label: 'En traitement', value: 0, color: '#00d68f' },
    { label: 'Urgents', value: 0, color: '#ff3d71' },
  ];

  ngOnInit(): void {
    this.updateDashboard();
  }

  get filteredDossiers(): CourrierDossier[] {
    if (this.statusFilter === 'Tous') {
      return this.dossiers;
    }

    return this.dossiers.filter((dossier) => dossier.statut === this.statusFilter);
  }

  updateDashboard(): void {
    this.stats[0].value = this.dossiers.length;
    this.stats[1].value = this.dossiers.filter((dossier) => dossier.statut === 'Réponse en attente').length;
    this.stats[2].value = this.dossiers.filter((dossier) => dossier.statut === 'En traitement').length;
    this.stats[3].value = this.dossiers.filter((dossier) => dossier.priorite === 'Urgente').length;
  }

  selectDossier(dossier: CourrierDossier): void {
    this.selectedDossier = dossier;
  }

  setWindowFilter(status: string): void {
    this.statusFilter = status;
  }

  getStatusColor(statut: string): string {
    switch (statut) {
      case 'Reçu':
        return 'info';
      case 'Transmis au DG':
        return 'primary';
      case 'En traitement':
        return 'success';
      case 'Soumis au DG':
        return 'warning';
      case 'Réponse en attente':
        return 'danger';
      case 'Traité':
        return 'basic';
      default:
        return 'basic';
    }
  }

  getPriorityClass(priorite: string): string {
    switch (priorite) {
      case 'Urgente':
        return 'priority-urgent';
      case 'Normale':
        return 'priority-normal';
      default:
        return 'priority-low';
    }
  }
}
