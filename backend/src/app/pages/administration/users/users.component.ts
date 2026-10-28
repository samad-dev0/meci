
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

import {
  AppUser,
  UtilisateurService
} from '../services/utilisateur.service';
import {
  AccessRightNode,
  AccessRightsService
} from '../../../auth/services/access-rights.service';

@Component({
  selector: 'ngx-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {

  utilisateurs: AppUser[] = [];
  droitsApplication: AccessRightNode[] = [];
  chargementDroits = false;
  catalogueDroitsCharge = false;
  peutGererDroits = false;

  utilisateurForm!: FormGroup;

  chargement = false;
  enregistrement = false;

  modeEdition = false;
  formulaireVisible = false;
  droitsUtilisateurCharges = true;

  utilisateurSelectionne: AppUser | null = null;

  messageSucces = '';
  messageErreur = '';
  private droitsSelectionnes: string[] = [];

  constructor(
    private fb: FormBuilder,
    private utilisateurService: UtilisateurService,
    private accessRights: AccessRightsService,
  ) {}

  ngOnInit(): void {

    this.creerFormulaire();

    this.chargerUtilisateurs();
    this.peutGererDroits = this.accessRights.isSuperAdmin();
    if (this.peutGererDroits) {
      this.chargerCatalogueDroits();
    }
  }


  // FORMULAIRE

  creerFormulaire(): void {

    this.utilisateurForm = this.fb.group({

      nom: [
        '',
        Validators.required
      ],

      prenom: [
        '',
        Validators.required
      ],

      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],

      telephone: [''],

      fonction: [''],

      service: [''],

      motDePasse: [
        '',
        Validators.required
      ],

      actif: [true]

    });
  }


  // CHARGER UTILISATEURS

  chargerUtilisateurs(): void {

    this.chargement = true;
    this.messageErreur = '';

    this.utilisateurService
      .getUtilisateurs()
      .subscribe({

        next: (data) => {

          this.utilisateurs = data;

          this.chargement = false;

        },

        error: (error) => {

          console.error(
            'Erreur chargement utilisateurs :',
            error
          );

          let messageErreur =
            error.error?.message || 'Impossible de charger les utilisateurs.';
          if (error.status === 401) {
            messageErreur = 'Votre session a expiré. Reconnectez-vous.';
          } else if (error.status === 403) {
            messageErreur = 'Vous n’avez pas l’autorisation de consulter les utilisateurs.';
          } else if (error.status === 0) {
            messageErreur = 'Le serveur est indisponible. Vérifiez que le service MECI est démarré.';
          }
          this.messageErreur = messageErreur;

          this.chargement = false;
        }

      });
  }

  chargerCatalogueDroits(): void {
    this.chargementDroits = true;
    this.accessRights.getCatalog().subscribe({
      next: (catalogue) => {
        this.droitsApplication = catalogue;
        this.preparerArbre(this.droitsApplication);
        this.catalogueDroitsCharge = true;
        this.chargementDroits = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement du catalogue des droits :', error);
        this.messageErreur = 'Impossible de charger les droits d’accès.';
        this.catalogueDroitsCharge = false;
        this.chargementDroits = false;
      },
    });
  }

  basculerDroit(node: AccessRightNode, checked: boolean): void {
    this.selectionnerBranche(node, checked);
    this.actualiserEtats(this.droitsApplication);
  }

  basculerDeveloppement(node: AccessRightNode): void {
    node.expanded = !node.expanded;
  }

  modifierDroits(utilisateur: AppUser): void {
    this.modifierUtilisateur(utilisateur);
  }

  ouvrirFormulaireAjout(): void {
    this.reinitialiserFormulaire();
    this.formulaireVisible = true;
  }

  private chargerDroitsUtilisateur(utilisateur: AppUser): void {
    if (!utilisateur.id) {
      this.droitsUtilisateurCharges = true;
      return;
    }

    const utilisateurId = utilisateur.id;
    this.droitsUtilisateurCharges = false;
    this.chargementDroits = true;
    this.utilisateurService.getAccessRights(utilisateurId).subscribe({
      next: (codes) => {
        if (this.utilisateurSelectionne?.id !== utilisateurId) {
          return;
        }
        this.droitsSelectionnes = codes;
        this.appliquerDroitsSelectionnes(this.droitsApplication);
        this.droitsUtilisateurCharges = true;
        this.chargementDroits = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des droits utilisateur :', error);
        this.messageErreur = 'Impossible de charger les droits de cet utilisateur.';
        this.chargementDroits = false;
      },
    });
  }

  private preparerArbre(nodes: AccessRightNode[]): void {
    nodes.forEach((node) => {
      node.selected = this.droitsSelectionnes.includes(node.code);
      node.expanded = node.children.length > 0;
      this.preparerArbre(node.children);
    });
    this.actualiserEtats(nodes);
  }

  private appliquerDroitsSelectionnes(nodes: AccessRightNode[]): void {
    nodes.forEach((node) => {
      node.selected = this.droitsSelectionnes.includes(node.code);
      this.appliquerDroitsSelectionnes(node.children);
    });
    this.actualiserEtats(nodes);
  }

  private selectionnerBranche(node: AccessRightNode, selected: boolean): void {
    node.selected = selected;
    node.children.forEach((child) => this.selectionnerBranche(child, selected));
  }

  private actualiserEtats(nodes: AccessRightNode[]): void {
    nodes.forEach((node) => {
      this.actualiserEtats(node.children);
      const hasChildren = node.children.length > 0;
      const allChildrenSelected = hasChildren
        && node.children.every((child) => child.checked && !child.indeterminate);
      const anyChildSelected = node.children.some(
        (child) => child.checked || child.indeterminate,
      );
      node.checked = node.selected || allChildrenSelected;
      node.indeterminate = hasChildren && anyChildSelected && !allChildrenSelected;
    });
  }

  private collectSelectedRights(nodes: AccessRightNode[]): string[] {
    return nodes.reduce<string[]>((codes, node) => {
      if (node.selected) {
        codes.push(node.code);
      }
      return codes.concat(this.collectSelectedRights(node.children));
    }, []);
  }


  // ENREGISTRER

  enregistrerUtilisateur(): void {

    this.messageSucces = '';
    this.messageErreur = '';

    if (!this.catalogueDroitsCharge) {
      this.messageErreur = 'Le catalogue des droits n’est pas disponible. Réessayez avant d’enregistrer.';
      return;
    }
    if (this.chargementDroits || (this.modeEdition && !this.droitsUtilisateurCharges)) {
      this.messageErreur = 'Attendez le chargement des droits avant d’enregistrer.';
      return;
    }

    if (this.utilisateurForm.invalid) {

      this.utilisateurForm.markAllAsTouched();

      return;
    }

    const utilisateur: AppUser = {
      ...this.utilisateurForm.value,
      requestedAccessRights: this.collectSelectedRights(this.droitsApplication),
    };

    this.enregistrement = true;


    // CREATION

    if (!this.modeEdition) {

      this.utilisateurService
        .creerUtilisateur(utilisateur)
        .subscribe({

          next: (nouvelUtilisateur) => {

            this.utilisateurs.unshift(
              nouvelUtilisateur
            );

            this.messageSucces =
              'Utilisateur créé avec succès.';

            this.reinitialiserFormulaire();

            this.enregistrement = false;
          },

          error: (error) => {

            console.error(error);

            const messageErreur =
              error?.error?.message ||
              error?.message ||
              'Erreur lors de la création de l’utilisateur.';

            this.messageErreur = messageErreur;

            this.enregistrement = false;
          }

        });

      return;
    }


    // ---------------------------------------------------
    // MODIFICATION
    // ---------------------------------------------------

    if (
      this.modeEdition &&
      this.utilisateurSelectionne?.id
    ) {

      const id =
        this.utilisateurSelectionne.id;

      this.utilisateurService
        .modifierUtilisateur(
          id,
          utilisateur
        )
        .subscribe({

          next: (utilisateurModifie) => {

            const index =
              this.utilisateurs.findIndex(
                u => u.id === id
              );

            if (index !== -1) {

              this.utilisateurs[index] =
                utilisateurModifie;
            }

            this.messageSucces =
              'Utilisateur modifié avec succès.';

            this.reinitialiserFormulaire();

            this.enregistrement = false;
          },

          error: (error) => {

            console.error(error);

            this.messageErreur =
              'Erreur lors de la modification.';

            this.enregistrement = false;
          }

        });
    }
  }


  // =====================================================
  // MODIFIER
  // =====================================================

  modifierUtilisateur(
    utilisateur: AppUser
  ): void {

    this.modeEdition = true;
    this.formulaireVisible = true;

    this.utilisateurSelectionne =
      utilisateur;

    this.utilisateurForm.patchValue({

      nom: utilisateur.nom,

      prenom: utilisateur.prenom,

      email: utilisateur.email,

      telephone: utilisateur.telephone,

      fonction: utilisateur.fonction,

      service: utilisateur.service,

      // motDePasse: utilisateur.motDePasse,

      actif: utilisateur.actif

    });
    const motDePasse = this.utilisateurForm.get('motDePasse');
    motDePasse?.clearValidators();
    motDePasse?.updateValueAndValidity();

    this.messageSucces = '';
    this.messageErreur = '';
    this.droitsSelectionnes = [];
    this.appliquerDroitsSelectionnes(this.droitsApplication);
    this.chargerDroitsUtilisateur(utilisateur);
  }


  // =====================================================
  // SUPPRIMER
  // =====================================================

  supprimerUtilisateur(
    utilisateur: AppUser
  ): void {

    if (!utilisateur.id) {
      return;
    }

    const confirmation = confirm(
      `Voulez-vous supprimer ${utilisateur.nom} ${utilisateur.prenom} ?`
    );

    if (!confirmation) {
      return;
    }

    this.utilisateurService
      .supprimerUtilisateur(utilisateur.id)
      .subscribe({

        next: () => {

          this.utilisateurs =
            this.utilisateurs.filter(
              u => u.id !== utilisateur.id
            );

          this.messageSucces =
            'Utilisateur supprimé avec succès.';
        },

        error: (error) => {

          console.error(error);

          this.messageErreur =
            'Impossible de supprimer cet utilisateur.';
        }

      });
  }


  // =====================================================
  // RESET
  // =====================================================

  reinitialiserFormulaire(): void {

    this.modeEdition = false;
    this.formulaireVisible = false;

    this.utilisateurSelectionne = null;
    this.droitsSelectionnes = [];
    this.droitsUtilisateurCharges = true;
    this.utilisateurForm.get('motDePasse')?.setValidators(Validators.required);
    this.utilisateurForm.get('motDePasse')?.updateValueAndValidity();
    this.appliquerDroitsSelectionnes(this.droitsApplication);

    this.utilisateurForm.reset({

      nom: '',
      prenom: '',
      email: '',
      telephone: '',
      fonction: '',
      service: '',
      motDePasse: '',
      actif: true

    });
  }

}
