import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

import {
  AppUser,
  UtilisateurService
} from './services/utilisateur.service';

@Component({
  selector: 'ngx-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.scss']
})
export class AdministrationComponent implements OnInit {

  utilisateurs: AppUser[] = [];

  utilisateurForm!: FormGroup;

  chargement = false;
  enregistrement = false;

  messageSucces = '';
  messageErreur = '';

  modeEdition = false;
  utilisateurSelectionne: AppUser | null = null;

  constructor(
    private fb: FormBuilder,
    private utilisateurService: UtilisateurService
  ) {}

  ngOnInit(): void {

    this.creerFormulaire();

    this.chargerUtilisateurs();
  }

  // FORMULAIRE

  creerFormulaire(): void {

    this.utilisateurForm = this.fb.group({

      nom: [
        '',
        [
          Validators.required,
          Validators.maxLength(100)
        ]
      ],

      prenom: [
        '',
        [
          Validators.required,
          Validators.maxLength(100)
        ]
      ],

      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],

      telephone: [
        ''
      ],

      fonction: [
        ''
      ],

      service: [
        ''
      ],

      actif: [
        true
      ]

    });
  }

  // CHARGER LES UTILISATEURS

  chargerUtilisateurs(): void {

    this.chargement = true;
    this.messageErreur = '';

    this.utilisateurService.getUtilisateurs()
      .subscribe({

        next: (utilisateurs) => {

          this.utilisateurs = utilisateurs;

          this.chargement = false;

        },

        error: (error) => {

          console.error(
            'Erreur lors du chargement des utilisateurs',
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

  // ENREGISTRER

  enregistrerUtilisateur(): void {

    this.messageSucces = '';
    this.messageErreur = '';

    if (this.utilisateurForm.invalid) {

      this.utilisateurForm.markAllAsTouched();

      return;
    }

    const utilisateur: AppUser =
      this.utilisateurForm.value;

    this.enregistrement = true;

    // CREATION

    if (!this.modeEdition) {

      this.utilisateurService
        .creerUtilisateur(utilisateur)
        .subscribe({

          next: (nouvelUtilisateur) => {

            this.utilisateurs.push(
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

    }

    // MODIFICATION

    else if (
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

  // MODIFIER

  modifierUtilisateur(
    utilisateur: AppUser
  ): void {

    this.modeEdition = true;

    this.utilisateurSelectionne =
      utilisateur;

    this.utilisateurForm.patchValue({

      nom: utilisateur.nom,

      prenom: utilisateur.prenom,

      email: utilisateur.email,

      telephone: utilisateur.telephone,

      fonction: utilisateur.fonction,

      service: utilisateur.service,

      actif: utilisateur.actif

    });

    this.messageSucces = '';
    this.messageErreur = '';
  }

  // SUPPRIMER

  supprimerUtilisateur(
    utilisateur: AppUser
  ): void {

    if (!utilisateur.id) {
      return;
    }

    const confirmation =
      confirm(
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

  // RESET

  reinitialiserFormulaire(): void {

    this.modeEdition = false;

    this.utilisateurSelectionne = null;

    this.utilisateurForm.reset({

      nom: '',

      prenom: '',

      email: '',

      telephone: '',

      fonction: '',

      service: '',

      actif: true

    });
  }

}
