import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AppUser, UtilisateurService } from '../../administration/services/utilisateur.service';

@Component({
	selector: 'ngx-initialisation',
	templateUrl: './initialisation.component.html',
	styleUrls: ['./initialisation.component.scss'],
})
export class InitialisationComponent implements OnInit {
  courrierForm!: FormGroup;

  origine: 'UPLOAD' | 'SCAN' | 'EMAIL' = 'UPLOAD';

  fichier: File | null = null;


  constructor(
    private fb: FormBuilder,
    private router: Router,
    private utilisateurService: UtilisateurService
  ) {}

  utilisateurs: AppUser[] = [];

  utilisateursFiltres: AppUser[] = [];

  rechercheDestinataire = '';

  triDestinataire: 'nom' | 'prenom' | 'fonction' | 'service' = 'nom';

  listeDestinatairesOuverte = false;

  destinataireSelectionne: AppUser | null = null;

  ngOnInit(): void {

    this.courrierForm = this.fb.group({

      typeDocument: [
        '',
        Validators.required
      ],

      dateReception: [
        this.dateAujourdhui(),
        Validators.required
      ],

      expediteur: [
        '',
        Validators.required
      ],

       destinataireId: [
        '',
        Validators.required
      ],

      reference: [
        this.genererReference()
      ],

      objet: [
        '',
        Validators.required
      ],

      description: [
        ''
      ],

      emailExpediteur: [
        ''
      ],

      dateEmail: [
        ''
      ]

    });
      this.chargerUtilisateurs();

  }

  // charger la liste des destinataires depuis le service
  chargerUtilisateurs(): void {

    this.utilisateurService.getUtilisateurs().subscribe({

      next: (utilisateurs) => {

        this.utilisateurs = utilisateurs.filter(
          utilisateur => utilisateur.actif
        );

        this.utilisateursFiltres = [...this.utilisateurs];

        this.trierDestinataires();

      },

      error: (error) => {

        console.error(
          'Erreur lors du chargement des utilisateurs :',
          error
        );

      }

    });

  }

  // rechercher les destinataires en fonction de la recherche
  rechercherDestinataire(): void {

  const recherche = this.rechercheDestinataire
    .trim()
    .toLowerCase();

  if (!recherche) {

    this.utilisateursFiltres = [...this.utilisateurs];

  } else {

    this.utilisateursFiltres = this.utilisateurs.filter(
      utilisateur =>
        utilisateur.nom.toLowerCase().includes(recherche) ||
        utilisateur.prenom.toLowerCase().includes(recherche) ||
        (utilisateur.fonction || '')
          .toLowerCase()
          .includes(recherche) ||
        (utilisateur.service || '')
          .toLowerCase()
          .includes(recherche)
    );

  }

  this.trierDestinataires();

}

  // trier les destinataires en fonction du critère de tri
  trierDestinataires(): void {

    this.utilisateursFiltres.sort((a, b) => {

      const valeurA = (
        a[this.triDestinataire] || ''
      ).toString().toLowerCase();

      const valeurB = (
        b[this.triDestinataire] || ''
      ).toString().toLowerCase();

      return valeurA.localeCompare(valeurB, 'fr');
    });

  }

  changerTriDestinataire(): void {

    this.trierDestinataires();

  }

  // sélectionner un destinataire dans la liste
  selectionnerDestinataire(
    utilisateur: AppUser
  ): void {

    this.destinataireSelectionne = utilisateur;

    this.courrierForm.patchValue({
      destinataireId: utilisateur.id
    });

    this.rechercheDestinataire =
      `${utilisateur.nom} ${utilisateur.prenom}`;

    this.listeDestinatairesOuverte = false;

  }

  // ouvrir ou fermer la liste des destinataires
  ouvrirListeDestinataires(): void {

    this.listeDestinatairesOuverte = true;

    this.rechercherDestinataire();

  }

  fermerListeDestinataires(): void {

    setTimeout(() => {

      this.listeDestinatairesOuverte = false;

    }, 200);

  }

  // reinitialiser le formulaire et les champs

  reinitialiserFormulaire(): void {

  this.courrierForm.reset({

    typeDocument: '',

    dateReception: this.dateAujourdhui(),

    expediteur: '',

    destinataireId: '',

    reference: this.genererReference(),

    objet: '',

    description: '',

    emailExpediteur: '',

    dateEmail: ''

  });

  this.origine = 'UPLOAD';

  this.fichier = null;


}

  genererReference(): string {

  const maintenant = new Date();

  const annee = maintenant.getFullYear();

  const mois = String(
    maintenant.getMonth() + 1
  ).padStart(2, '0');

  const jour = String(
    maintenant.getDate()
  ).padStart(2, '0');

  const heure = String(
    maintenant.getHours()
  ).padStart(2, '0');

  const minute = String(
    maintenant.getMinutes()
  ).padStart(2, '0');

  const seconde = String(
    maintenant.getSeconds()
  ).padStart(2, '0');

  return `MECI-${annee}${mois}${jour}-${heure}${minute}${seconde}`;

}


  changerOrigine(
    origine: 'UPLOAD' | 'SCAN' | 'EMAIL'
  ): void {

    this.origine = origine;

    this.fichier = null;

  }


  selectionnerFichier(event: Event): void {

    const input =
      event.target as HTMLInputElement;

    if (
      input.files &&
      input.files.length > 0
    ) {

      this.fichier = input.files[0];

    }

  }


  supprimerFichier(): void {

    this.fichier = null;

  }


  formatTaille(taille: number): string {

    if (taille < 1024) {

      return taille + ' octets';

    }

    if (taille < 1024 * 1024) {

      return (
        (taille / 1024).toFixed(1)
        + ' Ko'
      );

    }

    return (
      (taille / (1024 * 1024)).toFixed(1)
      + ' Mo'
    );

  }


  dateAujourdhui(): string {

    const date = new Date();

    return date
      .toISOString()
      .substring(0, 10);

  }


  enregistrerCourrier(): void {

    if (this.courrierForm.invalid) {

      this.courrierForm.markAllAsTouched();

      return;

    }


    const donnees = {

      ...this.courrierForm.value,

      origine: this.origine,

      fichier: this.fichier

    };


    console.log(
      'Courrier à enregistrer :',
      donnees
    );


    /*
     * Dans la prochaine étape,
     * nous allons envoyer ces données
     * vers Spring Boot avec FormData.
     */

  }


  retour(): void {

    this.router.navigate([
      '/courrier'
    ]);

  }
}
