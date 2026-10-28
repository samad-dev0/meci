import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../../auth/services/auth.service';
import { AppUser, UtilisateurService } from '../../administration/services/utilisateur.service';
import {
  AutorisationRapportService,
  PartageOwnerRequest,
  PartageUtilisateur,
} from '../../administration/services/autorisation-rapport.service';
import { Rapport } from '../../rapports/models/rapport.model';
import { RapportService } from '../../rapports/services/rapport.service';

@Component({
  selector: 'ngx-liste-rapport',
  templateUrl: './liste-rapport.component.html',
  styleUrls: ['./liste-rapport.component.scss'],
})
export class ListeRapportComponent implements OnInit {
  rapports: Rapport[] = [];
  rapportsFiltres: Rapport[] = [];
  dateFiltre: Date | null = null;
  chargement = false;
  messageErreur = '';
  peutPartager = false;
  chargementDroitPartage = true;
  panneauPartageOuvert = false;
  chargementPartage = false;
  enregistrementPartage = false;
  messageErreurPartage = '';
  messageSuccesPartage = '';
  utilisateursActifs: AppUser[] = [];
  partagesExistants: PartageUtilisateur[] = [];
  rapportsSelectionnesPourPartage = new Set<number>();
  beneficiairesSelectionnes = new Set<number>();
  private utilisateurConnecteId: number | null = null;

  constructor(
    private readonly rapportService: RapportService,
    private readonly autorisationService: AutorisationRapportService,
    private readonly utilisateurService: UtilisateurService,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.utilisateurConnecteId = this.authService.getUtilisateurConnecte()?.id || null;
    this.chargerRapports();
    this.autorisationService.peutPartager().subscribe({
      next: autorise => {
        this.peutPartager = autorise;
        this.chargementDroitPartage = false;
      },
      error: () => {
        this.chargementDroitPartage = false;
        this.messageErreurPartage = 'Impossible de vérifier votre autorisation de partage.';
      },
    });
  }

  chargerRapports(): void {
    this.chargement = true;
    this.messageErreur = '';
    this.rapportService.getMesRapports().subscribe({
      next: rapports => {
        this.rapports = rapports;
        this.appliquerFiltres();
        this.chargement = false;
      },
      error: () => {
        this.messageErreur = 'Impossible de charger vos rapports. Vérifiez votre connexion puis réessayez.';
        this.chargement = false;
      },
    });
  }

  appliquerFiltres(): void {
    this.rapportsFiltres = this.dateFiltre
      ? this.rapports.filter(rapport =>
        rapport.dateCreation != null &&
        new Date(rapport.dateCreation).toDateString() === this.dateFiltre!.toDateString(),
      )
      : [...this.rapports];
  }

  telechargerRapport(rapport: Rapport): void {
    if (rapport.id == null) {
      return;
    }
    this.messageErreur = '';
    this.rapportService.telechargerEtSauvegarder(rapport.id, `${rapport.titre || 'rapport'}.txt`).subscribe({
      error: () => {
        this.messageErreur = 'Impossible de télécharger ce rapport.';
      },
    });
  }

  filtrerParDate(): void {
    this.appliquerFiltres();
  }

  reinitialiserFiltre(): void {
    this.dateFiltre = null;
    this.appliquerFiltres();
  }

  supprimerRapport(rapport: Rapport): void {
    if (rapport.id == null || !confirm(`Voulez-vous vraiment supprimer le rapport « ${rapport.titre} » ?`)) {
      return;
    }
    this.rapportService.deleteRapport(rapport.id).subscribe({
      next: () => {
        this.rapports = this.rapports.filter(item => item.id !== rapport.id);
        this.appliquerFiltres();
      },
      error: () => {
        this.messageErreur = 'Impossible de supprimer ce rapport.';
      },
    });
  }

  ouvrirPanneauPartage(): void {
    if (!this.peutPartager || this.chargementPartage) {
      return;
    }
    this.panneauPartageOuvert = !this.panneauPartageOuvert;
    this.messageErreurPartage = '';
    this.messageSuccesPartage = '';
    if (!this.panneauPartageOuvert || this.utilisateursActifs.length > 0) {
      return;
    }

    this.chargementPartage = true;
    forkJoin({
      utilisateurs: this.utilisateurService.getUtilisateurs(),
      partages: this.autorisationService.getMesPartages(),
    }).subscribe({
      next: ({ utilisateurs, partages }) => {
        this.utilisateursActifs = utilisateurs.filter(user =>
          user.id != null && user.id !== this.utilisateurConnecteId && user.actif !== false,
        );
        this.partagesExistants = partages;
        this.chargementPartage = false;
      },
      error: () => {
        this.messageErreurPartage = 'Impossible de charger les utilisateurs ou les partages existants.';
        this.chargementPartage = false;
      },
    });
  }

  toggleRapportPourPartage(rapportId: number): void {
    const selection = new Set(this.rapportsSelectionnesPourPartage);
    selection.has(rapportId) ? selection.delete(rapportId) : selection.add(rapportId);
    this.rapportsSelectionnesPourPartage = selection;
  }

  toggleBeneficiairePourPartage(utilisateurId: number): void {
    const selection = new Set(this.beneficiairesSelectionnes);
    selection.has(utilisateurId) ? selection.delete(utilisateurId) : selection.add(utilisateurId);
    this.beneficiairesSelectionnes = selection;
  }

  enregistrerPartage(): void {
    if (this.utilisateurConnecteId == null ||
        this.rapportsSelectionnesPourPartage.size === 0 ||
        this.beneficiairesSelectionnes.size === 0) {
      this.messageErreurPartage = 'Sélectionnez au moins un rapport et un utilisateur.';
      return;
    }
    const demande: PartageOwnerRequest = {
      proprietaireId: this.utilisateurConnecteId,
      autorisations: Array.from(this.beneficiairesSelectionnes).map(beneficiaireId => ({
        beneficiaireId,
        rapportIds: Array.from(this.rapportsSelectionnesPourPartage),
      })),
    };
    this.enregistrementPartage = true;
    this.messageErreurPartage = '';
    this.messageSuccesPartage = '';
    this.autorisationService.accorderMesPartages([demande]).subscribe({
      next: () => {
        this.rapportsSelectionnesPourPartage = new Set<number>();
        this.beneficiairesSelectionnes = new Set<number>();
        this.messageSuccesPartage = 'Les rapports sélectionnés ont été partagés.';
        this.enregistrementPartage = false;
        this.autorisationService.getMesPartages().subscribe({
          next: partages => this.partagesExistants = partages,
          error: () => this.messageErreurPartage = 'Le partage est enregistré, mais la liste des accès n’a pas pu être actualisée.',
        });
      },
      error: error => {
        this.messageErreurPartage = error?.error?.message || 'Impossible de partager les rapports sélectionnés.';
        this.enregistrementPartage = false;
      },
    });
  }

  retirerPartage(partage: PartageUtilisateur): void {
    if (partage.rapportId == null) {
      return;
    }
    this.messageErreurPartage = '';
    this.autorisationService.retirerPartage(
      partage.proprietaireId,
      partage.beneficiaireId,
      partage.rapportId,
    ).subscribe({
      next: () => {
        this.partagesExistants = this.partagesExistants.filter(item =>
          !(item.proprietaireId === partage.proprietaireId &&
            item.beneficiaireId === partage.beneficiaireId &&
            item.rapportId === partage.rapportId),
        );
      },
      error: () => this.messageErreurPartage = 'Impossible de retirer cet accès.',
    });
  }

  nomBeneficiaire(id: number): string {
    const utilisateur = this.utilisateursActifs.find(user => user.id === id);
    return utilisateur ? `${utilisateur.prenom} ${utilisateur.nom}` : `Utilisateur #${id}`;
  }
}
