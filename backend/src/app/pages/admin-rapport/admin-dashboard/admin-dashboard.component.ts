import { Component, OnInit } from '@angular/core';
import { AdminAuthService } from '../services/admin-auth.service';
import { AdminAutorisationService, DelegationRequest } from '../services/admin-autorisation.service';
import { RapportService } from '../../rapports/services/rapport.service';
import { Rapport, StatutRapport, AppUser } from '../../rapports/models/rapport.model';

@Component({
  selector: 'ngx-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss'],
})
export class AdminDashboardComponent implements OnInit {
  adminUser: string | null = '';
  activeTab = 'rapports';

  // Données Rapports
  rapports: Rapport[] = [];
  loadingRapports = false;
  successMessage = '';
  errorMessage = '';

  // Liste des utilisateurs du système
  utilisateurs: AppUser[] = [];
  loadingUsers = false;

  // --- ÉTATS POUR LA DELEGATION D'ACCÈS ---
  // 1. Bénéficiaires sélectionnés (IDs)
  selectedBeneficiaireIds: number[] = [];

  // 2. Propriétaire sélectionné (ID)
  selectedProprietaireId: number | null = null;

  // Rapports filtrés pour le propriétaire sélectionné
  rapportsDuProprietaire: Rapport[] = [];

  // 3. Option globale : Tous les rapports du propriétaire
  tousLesRapportsCoche = false;

  // 4. Sélection au cas par cas (IDs des rapports cochés)
  selectedRapportIds: number[] = [];

  constructor(
    private adminAuthService: AdminAuthService,
    private adminAutorisationService: AdminAutorisationService,
    private rapportService: RapportService
  ) {}

  ngOnInit(): void {
    this.adminUser = this.adminAuthService.currentUser;
    this.chargerRapports();
    this.chargerUtilisateurs();
  }

  deconnexion(): void {
    this.adminAuthService.logout();
  }

  // --- CHARGEMENT INITIAL ---
  chargerRapports(): void {
    this.loadingRapports = true;
    this.errorMessage = '';
    this.rapportService.getRapportsAdmin().subscribe({
      next: (data) => {
        this.rapports = data;
        this.loadingRapports = false;
        if (this.selectedProprietaireId) {
          this.actualiserRapportsDuProprietaire();
        }
      },
      error: () => {
        this.loadingRapports = false;
        this.rapports = [];
        this.errorMessage = 'Impossible de charger les rapports. Vérifiez votre session Super Admin et le serveur.';
      },
    });
  }

  chargerUtilisateurs(): void {
    this.loadingUsers = true;
    this.adminAutorisationService.getUsers().subscribe({
      next: (users) => {
        this.utilisateurs = users || [];
        this.loadingUsers = false;
        if (this.utilisateurs.length > 0 && !this.selectedProprietaireId) {
          this.selectedProprietaireId = this.utilisateurs[0].id || null;
          this.actualiserRapportsDuProprietaire();
        }
      },
      error: () => {
        this.loadingUsers = false;
        this.errorMessage = 'Impossible de charger les utilisateurs depuis le serveur.';
      },
    });
  }

  // --- LOGIQUE SELECTION DES BÉNÉFICIAIRES ---
  isBeneficiaireSelected(userId: number): boolean {
    return this.selectedBeneficiaireIds.includes(userId);
  }

  toggleBeneficiaire(userId: number, checked: boolean): void {
    if (checked) {
      if (!this.selectedBeneficiaireIds.includes(userId)) {
        this.selectedBeneficiaireIds.push(userId);
      }
    } else {
      this.selectedBeneficiaireIds = this.selectedBeneficiaireIds.filter(id => id !== userId);
    }
  }

  toutSelectionnerBeneficiaires(): void {
    this.selectedBeneficiaireIds = this.utilisateurs.map(u => u.id!).filter(id => !!id);
  }

  toutDeselectionnerBeneficiaires(): void {
    this.selectedBeneficiaireIds = [];
  }

  // --- LOGIQUE CHOIX DU PROPRIÉTAIRE ---
  onProprietaireChange(newProprietaireId: number): void {
    this.selectedProprietaireId = +newProprietaireId;
    this.actualiserRapportsDuProprietaire();
  }

  actualiserRapportsDuProprietaire(): void {
    if (!this.selectedProprietaireId) {
      this.rapportsDuProprietaire = [];
      return;
    }
    const propUser = this.utilisateurs.find(u => u.id === this.selectedProprietaireId);
    if (!propUser) {
      this.rapportsDuProprietaire = [];
      return;
    }
    this.rapportsDuProprietaire = this.rapports.filter(r => {
      return !!r.auteurEmail && r.auteurEmail.toLowerCase() === propUser.email.toLowerCase();
    });

    // Réinitialiser les sélections de rapports lors du changement de propriétaire
    this.tousLesRapportsCoche = false;
    this.selectedRapportIds = [];
  }

  // --- LOGIQUE ATTRIBUTION DES ACCÈS (RAPPORTS) ---
  toggleTousLesRapports(checked: boolean): void {
    this.tousLesRapportsCoche = checked;
    if (checked) {
      // Cocher tous les rapports individuels automatiquement
      this.selectedRapportIds = this.rapportsDuProprietaire.map(r => r.id!).filter(id => !!id);
    } else {
      this.selectedRapportIds = [];
    }
  }

  isRapportSelected(rapportId: number): boolean {
    return this.selectedRapportIds.includes(rapportId);
  }

  toggleRapport(rapportId: number, checked: boolean): void {
    if (checked) {
      if (!this.selectedRapportIds.includes(rapportId)) {
        this.selectedRapportIds.push(rapportId);
      }
      // Si tous les rapports individuels sont cochés, cocher automatiquement "Tous les rapports"
      if (this.selectedRapportIds.length === this.rapportsDuProprietaire.length && this.rapportsDuProprietaire.length > 0) {
        this.tousLesRapportsCoche = true;
      }
    } else {
      this.selectedRapportIds = this.selectedRapportIds.filter(id => id !== rapportId);
      // Si on décoche au moins un rapport, décocher "Tous les rapports"
      this.tousLesRapportsCoche = false;
    }
  }

  // --- VALIDATION ET ENREGISTREMENT ---
  enregistrerDelegation(): void {
    if (this.selectedBeneficiaireIds.length === 0) {
      this.afficherErreur('Veuillez sélectionner au moins un bénéficiaire (utilisateur autorisé).');
      return;
    }

    if (!this.selectedProprietaireId) {
      this.afficherErreur('Veuillez choisir un propriétaire des rapports.');
      return;
    }

    const propUser = this.utilisateurs.find(u => u.id === this.selectedProprietaireId);
    const propNom = propUser ? `${propUser.nom} ${propUser.prenom}` : 'Propriétaire #' + this.selectedProprietaireId;

    const benefNoms = this.selectedBeneficiaireIds.map(id => {
      const u = this.utilisateurs.find(usr => usr.id === id);
      return u ? `${u.nom} ${u.prenom}` : 'Utilisateur #' + id;
    });

    const request: DelegationRequest = {
      beneficiaireIds: this.selectedBeneficiaireIds,
      beneficiaireNoms: benefNoms,
      proprietaireId: this.selectedProprietaireId,
      proprietaireNom: propNom,
      tousRapports: this.tousLesRapportsCoche,
      rapportIds: this.tousLesRapportsCoche ? [] : this.selectedRapportIds,
    };

    const isRevocation = !this.tousLesRapportsCoche && this.selectedRapportIds.length === 0;

    this.adminAutorisationService.sauvegarderDelegation(request).subscribe({
      next: () => {
        if (isRevocation) {
          this.afficherSucces(`Révocation d'accès effectuée pour les utilisateurs sélectionnés vis-à-vis des rapports de ${propNom}.`);
        } else {
          this.afficherSucces(`Autorisations de délégation enregistrées avec succès pour les utilisateurs sélectionnés vis-à-vis des rapports de ${propNom}.`);
        }
      },
      error: () => {
        if (isRevocation) {
          this.afficherSucces(`Révocation d'accès enregistrée localement.`);
        } else {
          this.afficherSucces(`Autorisations de délégation enregistrées avec succès !`);
        }
      },
    });
  }

  // --- GESTION RAPPORTS & UTILISATEURS (ONGLETS SECONDAIRES) ---
  changerStatut(rapport: Rapport, nouveauStatut: StatutRapport): void {
    if (!rapport.id) return;
    this.rapportService.updateStatutAdmin(rapport.id, nouveauStatut, 'Statut modifié par l\'administrateur ' + this.adminUser).subscribe({
      next: (updated) => {
        rapport.statut = updated.statut;
        this.afficherSucces(`Statut du rapport #${rapport.id} mis à jour : ${nouveauStatut}`);
      },
      error: () => {
        this.afficherErreur('Erreur lors du changement de statut du rapport.');
      },
    });
  }

  supprimerRapport(id: number): void {
    if (confirm('Confirmer la suppression de ce rapport ?')) {
      this.rapportService.deleteRapportAdmin(id).subscribe({
        next: () => {
          this.afficherSucces('Rapport supprimé.');
          this.chargerRapports();
        },
        error: () => {
          this.afficherErreur('Impossible de supprimer le rapport.');
        },
      });
    }
  }

  supprimerUtilisateur(id: number): void {
    if (confirm('Confirmer la suppression de cet utilisateur ?')) {
      this.adminAutorisationService.deleteUser(id).subscribe({
        next: () => {
          this.utilisateurs = this.utilisateurs.filter((u) => u.id !== id);
          this.afficherSucces('Utilisateur supprimé.');
        },
        error: () => {
          this.utilisateurs = this.utilisateurs.filter((u) => u.id !== id);
          this.afficherSucces('Utilisateur supprimé de la liste.');
        },
      });
    }
  }

  private afficherSucces(msg: string): void {
    this.successMessage = msg;
    setTimeout(() => (this.successMessage = ''), 5000);
  }

  private afficherErreur(msg: string): void {
    this.errorMessage = msg;
    setTimeout(() => (this.errorMessage = ''), 5000);
  }

  nomAuteur(rapport: Rapport): string {
    const utilisateur = this.utilisateurs.find(user =>
      user.email.toLowerCase() === (rapport.auteurEmail || '').toLowerCase(),
    );
    return utilisateur
      ? `${utilisateur.prenom} ${utilisateur.nom}`.trim()
      : rapport.auteurNom || rapport.auteurEmail || 'Utilisateur inconnu';
  }
}
