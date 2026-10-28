import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin, of } from 'rxjs';
import { AuthService } from '../../../auth/services/auth.service';
import { AppUser, UtilisateurService } from '../services/utilisateur.service';
import {
  AutorisationRapportService,
  PartageOwnerRequest,
  PartageRapport,
  RetraitPartageRequest,
} from '../services/autorisation-rapport.service';
import { Rapport } from '../../rapports/models/rapport.model';

type AutorisationMode = 'gestionnaires' | 'partageurs' | 'partager' | 'retirer';

@Component({
  selector: 'ngx-autorise-rapport',
  templateUrl: './autorise-rapport.component.html',
  styleUrls: ['./autorise-rapport.component.scss'],
})
export class AutoriseRapportComponent implements OnInit {
  utilisateurs: AppUser[] = [];
  partages: PartageRapport[] = [];
  rapportsProprietaire: Rapport[] = [];
  mode: AutorisationMode | null = null;
  proprietaireIdSelectionne: number | null = null;
  nouveauxGestionnaires = new Set<number>();
  nouveauxPartageurs = new Set<number>();
  rapportsCochesParProprietaire = new Map<number, Set<number>>();
  beneficiairesSelectionnes = new Set<number>();
  partagesAretirer = new Set<string>();
  superAdminIdSelectionne: number | null = null;
  rechercheProprietaire = '';
  rechercheBeneficiaire = '';
  rechercheRapport = '';
  rechercheRetrait = '';
  estSuperAdmin = false;
  peutGererPartages = false;
  chargement = false;
  chargementRapports = false;
  enregistrement = false;
  messageSucces = '';
  messageErreur = '';

  private utilisateurConnecteId: number | null = null;

  constructor(
    private readonly authService: AuthService,
    private readonly utilisateurService: UtilisateurService,
    private readonly autorisationService: AutorisationRapportService,
  ) {}

  ngOnInit(): void {
    this.utilisateurConnecteId = this.authService.getUtilisateurConnecte()?.id || null;
    if (!this.utilisateurConnecteId) {
      this.messageErreur = 'Connectez-vous pour accéder à la gestion des autorisations.';
      return;
    }
    this.chargement = true;
    this.utilisateurService.getUtilisateurs().subscribe({
      next: utilisateurs => {
        this.utilisateurs = utilisateurs;
        const connecte = utilisateurs.find(user => user.id === this.utilisateurConnecteId);
        this.estSuperAdmin = connecte?.superAdmin === true;
        this.peutGererPartages = this.estSuperAdmin || connecte?.gestionnairePartage === true;
        this.chargement = false;
        this.chargerDonneesAutorisation();
      },
      error: error => {
        this.afficherErreur(error, 'Impossible de charger les utilisateurs.');
        this.chargement = false;
      },
    });
  }

  private chargerDonneesAutorisation(): void {
    if (!this.peutGererPartages) {
      return;
    }
    const gestionnaires$ = this.estSuperAdmin
      ? this.autorisationService.getGestionnaires()
      : of([] as number[]);
    const partageurs$ = this.estSuperAdmin
      ? this.autorisationService.getUtilisateursAutorisesAuPartage()
      : of([] as number[]);
    forkJoin({
      gestionnaires: gestionnaires$,
      partageurs: partageurs$,
      partages: this.autorisationService.getPartages(),
    }).subscribe({
      next: ({ gestionnaires, partageurs, partages }) => {
        this.nouveauxGestionnaires = new Set(gestionnaires);
        this.nouveauxPartageurs = new Set(partageurs);
        this.partages = partages;
      },
      error: error => this.afficherErreur(error, 'Impossible de charger les autorisations enregistrées.'),
    });
  }

  choisirMode(mode: AutorisationMode): void {
    this.messageErreur = '';
    this.messageSucces = '';
    this.mode = mode;
    if (mode === 'gestionnaires') {
      this.nouveauxGestionnaires = new Set(
        this.utilisateurs
          .filter(user => user.gestionnairePartage)
          .map(user => user.id!)
          .filter(id => id != null),
      );
    }
  }

  toggleGestionnaire(userId: number): void {
    const next = new Set(this.nouveauxGestionnaires);
    if (next.has(userId)) {
      next.delete(userId);
    } else {
      next.add(userId);
    }
    this.nouveauxGestionnaires = next;
  }

  togglePartageur(userId: number): void {
    const next = new Set(this.nouveauxPartageurs);
    if (next.has(userId)) {
      next.delete(userId);
    } else {
      next.add(userId);
    }
    this.nouveauxPartageurs = next;
  }

  enregistrerPartageurs(): void {
    this.enregistrement = true;
    this.messageErreur = '';
    this.autorisationService.enregistrerUtilisateursAutorisesAuPartage(
      Array.from(this.nouveauxPartageurs),
    ).subscribe({
      next: () => {
        this.utilisateurs = this.utilisateurs.map(user => ({
          ...user,
          peutPartagerRapports: user.id != null && this.nouveauxPartageurs.has(user.id),
        }));
        this.messageSucces = 'Les autorisations de partage individuel ont été mises à jour.';
        this.enregistrement = false;
      },
      error: error => {
        this.afficherErreur(error, 'Impossible d’enregistrer les autorisations de partage.');
        this.enregistrement = false;
      },
    });
  }

  enregistrerGestionnaires(): void {
    this.enregistrement = true;
    this.messageErreur = '';
    this.autorisationService.enregistrerGestionnaires(Array.from(this.nouveauxGestionnaires)).subscribe({
      next: () => {
        this.utilisateurs = this.utilisateurs.map(user => ({
          ...user,
          gestionnairePartage: user.id != null && this.nouveauxGestionnaires.has(user.id),
        }));
        this.messageSucces = 'Les droits de gestion des partages ont été mis à jour.';
        this.enregistrement = false;
      },
      error: error => {
        this.afficherErreur(error, 'Impossible d’enregistrer les gestionnaires.');
        this.enregistrement = false;
      },
    });
  }

  transfererSuperAdmin(): void {
    if (!this.superAdminIdSelectionne) {
      this.messageErreur = 'Sélectionnez le nouvel utilisateur Super Admin.';
      return;
    }
    this.enregistrement = true;
    this.messageErreur = '';
    this.autorisationService.transfererSuperAdmin(this.superAdminIdSelectionne).subscribe({
      next: () => {
        this.utilisateurs = this.utilisateurs.map(user => ({
          ...user,
          superAdmin: user.id === this.superAdminIdSelectionne,
          gestionnairePartage: user.id === this.superAdminIdSelectionne
            ? false
            : user.gestionnairePartage,
        }));
        this.estSuperAdmin = false;
        this.peutGererPartages = this.utilisateurs.some(user =>
          user.id === this.utilisateurConnecteId && user.gestionnairePartage === true,
        );
        this.messageSucces = 'Le rôle Super Admin a été transféré. Le nouvel administrateur peut maintenant gérer les droits.';
        this.enregistrement = false;
      },
      error: error => {
        this.afficherErreur(error, 'Impossible de transférer le rôle Super Admin.');
        this.enregistrement = false;
      },
    });
  }

  selectionnerProprietaire(user: AppUser): void {
    if (user.id == null) {
      return;
    }
    this.messageErreur = '';
    this.messageSucces = '';
    this.proprietaireIdSelectionne = user.id;
    if (!this.rapportsCochesParProprietaire.has(user.id)) {
      this.rapportsCochesParProprietaire.set(user.id, new Set<number>());
    }
    this.chargerRapportsProprietaire();
  }

  chargerRapportsProprietaire(): void {
    this.rapportsProprietaire = [];
    if (!this.proprietaireIdSelectionne) {
      return;
    }
    this.chargementRapports = true;
    this.autorisationService.getRapportsDuProprietaire(this.proprietaireIdSelectionne).subscribe({
      next: rapports => {
        this.rapportsProprietaire = rapports;
        this.chargementRapports = false;
      },
      error: error => {
        this.afficherErreur(error, 'Impossible de charger les rapports de ce propriétaire.');
        this.chargementRapports = false;
      },
    });
  }

  toggleRapport(rapportId: number): void {
    if (!this.proprietaireIdSelectionne) {
      return;
    }
    const selection = new Set(this.rapportsCochesParProprietaire.get(this.proprietaireIdSelectionne) || []);
    if (selection.has(rapportId)) {
      selection.delete(rapportId);
    } else {
      selection.add(rapportId);
    }
    this.rapportsCochesParProprietaire.set(this.proprietaireIdSelectionne, selection);
    this.rapportsCochesParProprietaire = new Map(this.rapportsCochesParProprietaire);
  }

  isRapportSelectionne(rapportId: number): boolean {
    if (!this.proprietaireIdSelectionne) {
      return false;
    }
    return this.rapportsCochesParProprietaire.get(this.proprietaireIdSelectionne)?.has(rapportId) === true;
  }

  toggleBeneficiaire(userId: number): void {
    const next = new Set(this.beneficiairesSelectionnes);
    if (next.has(userId)) {
      next.delete(userId);
    } else {
      next.add(userId);
    }
    this.beneficiairesSelectionnes = next;
  }

  isBeneficiaireSelectionne(userId: number): boolean {
    return this.beneficiairesSelectionnes.has(userId);
  }

  accorderAcces(): void {
    const proprietairesAvecRapports = this.proprietairesAvecRapportsSelectionnes;
    if (proprietairesAvecRapports.length === 0) {
      this.messageErreur = 'Cliquez sur une personne, puis cochez uniquement les rapports à partager. Tous les rapports d’une personne ne sont jamais accordés automatiquement.';
      return;
    }
    if (this.beneficiairesSelectionnes.size === 0) {
      this.messageErreur = 'Sélectionnez au moins une personne qui pourra consulter les rapports choisis.';
      return;
    }

    const partages: PartageOwnerRequest[] = [];
    for (const proprietaireId of proprietairesAvecRapports) {
      const rapportIds = Array.from(this.rapportsCochesParProprietaire.get(proprietaireId) || []);
      const autorisations = Array.from(this.beneficiairesSelectionnes)
        .filter(beneficiaireId => beneficiaireId !== proprietaireId)
        .map(beneficiaireId => ({ beneficiaireId, rapportIds }));
      if (autorisations.length > 0) {
        partages.push({ proprietaireId, autorisations });
      }
    }
    if (partages.length === 0) {
      this.messageErreur = 'Une personne ne peut pas recevoir l’accès à ses propres rapports. Choisissez un autre bénéficiaire.';
      return;
    }

    this.enregistrement = true;
    this.messageErreur = '';
    this.autorisationService.accorderPartages(partages).subscribe({
      next: () => {
        this.messageSucces = 'L’accès a été accordé uniquement aux rapports sélectionnés.';
        this.enregistrement = false;
        this.rapportsCochesParProprietaire = new Map();
        this.beneficiairesSelectionnes = new Set();
        this.chargerDonneesAutorisation();
        if (this.proprietaireIdSelectionne) {
          this.rapportsCochesParProprietaire.set(this.proprietaireIdSelectionne, new Set<number>());
        }
      },
      error: error => {
        this.afficherErreur(error, 'Impossible d’accorder ces accès.');
        this.enregistrement = false;
      },
    });
  }

  togglePartageARetirer(partage: PartageRapport): void {
    if (partage.rapportId == null) {
      return;
    }
    const key = this.clePartage(partage);
    const next = new Set(this.partagesAretirer);
    if (next.has(key)) {
      next.delete(key);
    } else {
      next.add(key);
    }
    this.partagesAretirer = next;
  }

  partageEstSelectionnePourRetrait(partage: PartageRapport): boolean {
    return this.partagesAretirer.has(this.clePartage(partage));
  }

  retirerPartagesSelectionnes(): void {
    const selection = this.partages.filter(partage =>
      partage.rapportId != null && this.partagesAretirer.has(this.clePartage(partage)),
    );
    if (selection.length === 0) {
      this.messageErreur = 'Sélectionnez au moins un droit à retirer.';
      return;
    }
    this.enregistrement = true;
    this.messageErreur = '';
    const retraits: RetraitPartageRequest[] = selection.map(partage => ({
      proprietaireId: partage.proprietaireId,
      beneficiaireId: partage.beneficiaireId,
      rapportId: partage.rapportId!,
    }));
    this.autorisationService.retirerPartages(retraits).subscribe({
      next: () => {
        const keys = new Set(selection.map(partage => this.clePartage(partage)));
        this.partages = this.partages.filter(partage => !keys.has(this.clePartage(partage)));
        this.partagesAretirer = new Set();
        this.messageSucces = 'Les droits sélectionnés ont été retirés.';
        this.enregistrement = false;
      },
      error: error => {
        this.afficherErreur(error, 'Impossible de retirer les droits sélectionnés.');
        this.enregistrement = false;
      },
    });
  }

  get proprietairesFiltres(): AppUser[] {
    const terme = this.rechercheProprietaire.trim().toLowerCase();
    return this.utilisateurs.filter(user => this.correspondRecherche(user, terme));
  }

  get beneficiairesFiltres(): AppUser[] {
    const terme = this.rechercheBeneficiaire.trim().toLowerCase();
    return this.utilisateurs.filter(user =>
      user.id != null &&
      user.actif !== false &&
      this.correspondRecherche(user, terme),
    );
  }

  get rapportsFiltres(): Rapport[] {
    const terme = this.rechercheRapport.trim().toLowerCase();
    if (!terme) {
      return this.rapportsProprietaire;
    }
    return this.rapportsProprietaire.filter(rapport =>
      (rapport.titre || '').toLowerCase().includes(terme),
    );
  }

  get partagesFiltres(): PartageRapport[] {
    const terme = this.rechercheRetrait.trim().toLowerCase();
    if (!terme) {
      return this.partages;
    }
    return this.partages.filter(partage =>
      (partage.rapportTitre || '').toLowerCase().includes(terme) ||
      this.formatUtilisateur(partage.proprietaireId).toLowerCase().includes(terme) ||
      this.formatUtilisateur(partage.beneficiaireId).toLowerCase().includes(terme),
    );
  }

  get utilisateursSuperAdminPossibles(): AppUser[] {
    return this.utilisateurs.filter(user => user.actif !== false && user.superAdmin !== true);
  }

  get nombrePartagesSelectionnesPourRetrait(): number {
    return this.partagesAretirer.size;
  }

  get proprietairesAvecRapportsSelectionnes(): number[] {
    return Array.from(this.rapportsCochesParProprietaire.entries())
      .filter(([, rapportIds]) => rapportIds.size > 0)
      .map(([proprietaireId]) => proprietaireId);
  }

  get nombreRapportsSelectionnes(): number {
    let total = 0;
    this.rapportsCochesParProprietaire.forEach(rapportIds => {
      total += rapportIds.size;
    });
    return total;
  }

  get proprietaireActif(): AppUser | undefined {
    return this.utilisateurs.find(user => user.id === this.proprietaireIdSelectionne);
  }

  nombreRapportsCoches(userId: number | undefined): number {
    if (userId == null) {
      return 0;
    }
    return this.rapportsCochesParProprietaire.get(userId)?.size || 0;
  }

  nombrePartagesDuRapport(rapportId: number | undefined): number {
    if (rapportId == null) {
      return 0;
    }
    return this.partages.filter(partage => partage.rapportId === rapportId).length;
  }

  initiales(user: AppUser): string {
    return `${(user.prenom || '').charAt(0)}${(user.nom || '').charAt(0)}`.toUpperCase();
  }

  formatUtilisateur(id: number): string {
    const user = this.utilisateurs.find(item => item.id === id);
    return user ? `${user.prenom} ${user.nom} (${user.email})` : `Utilisateur #${id}`;
  }

  private correspondRecherche(user: AppUser, terme: string): boolean {
    if (!terme) {
      return true;
    }
    return `${user.prenom} ${user.nom} ${user.email}`.toLowerCase().includes(terme);
  }

  private clePartage(partage: PartageRapport): string {
    return `${partage.proprietaireId}:${partage.beneficiaireId}:${partage.rapportId}`;
  }

  private afficherErreur(error: HttpErrorResponse, fallback: string): void {
    const payload = error.error;
    const apiMessage = typeof payload === 'string'
      ? payload
      : payload?.message || payload?.error;
    if (typeof apiMessage === 'string' && apiMessage.trim()) {
      this.messageErreur = apiMessage;
      return;
    }
    if (error.status === 0) {
      this.messageErreur = 'Le serveur est injoignable. Vérifiez que le backend MECI est démarré.';
      return;
    }
    if (error.status === 401) {
      this.messageErreur = 'Votre session a expiré. Déconnectez-vous puis reconnectez-vous.';
      return;
    }
    if (error.status === 403) {
      this.messageErreur = 'Votre compte n’a plus le droit de gérer les partages. Reconnectez-vous ou demandez au Super Admin de vérifier vos droits.';
      return;
    }
    this.messageErreur = error.status
      ? `${fallback} (erreur serveur ${error.status}).`
      : fallback;
  }
}
