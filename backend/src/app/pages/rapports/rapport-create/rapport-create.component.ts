import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RapportService } from '../services/rapport.service';
import { Rapport, TypeRapport } from '../models/rapport.model';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'ngx-rapport-create',
  templateUrl: './rapport-create.component.html',
  styleUrls: ['./rapport-create.component.scss'],
})
export class RapportCreateComponent {
  rapport: Rapport = {
    titre: '',
    typeRapport: 'RAPPORT_ACTIVITE',
    auteurNom: '',
    auteurEmail: '',
    contenu: '',
    verificateursAssignes: '',
    nomFichier: '',
    cheminFichier: '',
    fileSize: 0,
  };

  selectedDocuments: File[] = [];
  fileErrorMessage = '';
  allowedAcceptString =
    '.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.odt,.ods,.odp,.rtf,.csv';

  allowedExtensions = [
    '.pdf',
    '.doc',
    '.docx',
    '.xls',
    '.xlsx',
    '.ppt',
    '.pptx',
    '.txt',
    '.odt',
    '.ods',
    '.odp',
    '.rtf',
    '.csv',
  ];

  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private rapportService: RapportService,
    private router: Router,
    private authService: AuthService
  ) {
    const utilisateur = this.authService.getUtilisateurConnecte();
    if (utilisateur) {
      this.rapport.auteurNom = `${utilisateur.prenom} ${utilisateur.nom}`.trim();
      this.rapport.auteurEmail = utilisateur.email;
    }
  }

  onFileChange(event: any): void {
    const files: FileList = event.target.files;

    if (!files || files.length === 0) {
      return;
    }

    this.fileErrorMessage = '';

    const newFiles: File[] = [];
    const rejectedFiles: string[] = [];

    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      const ext =
        '.' + (file.name.split('.').pop() || '').toLowerCase();

      const isImage = file.type.startsWith('image/');
      const isAllowedDoc =
        this.allowedExtensions.includes(ext) && !isImage;

      if (isAllowedDoc) {
        const alreadySelected = this.selectedDocuments.some(
          (f) => f.name === file.name && f.size === file.size
        );

        if (!alreadySelected) {
          newFiles.push(file);
        }
      } else {
        rejectedFiles.push(file.name);
      }
    }

    if (rejectedFiles.length > 0) {
      this.fileErrorMessage =
        `Le(s) fichier(s) suivant(s) ne sont pas des documents autorisés et ont été rejetés : ${rejectedFiles.join(
          ', '
        )}. Seuls les documents (PDF, Word, Excel, PowerPoint, TXT, ODT, RTF, CSV) sont acceptés (les images et autres formats sont interdits).`;
    }

    this.selectedDocuments = [
      ...this.selectedDocuments,
      ...newFiles,
    ];

    this.updateNomFichier();

    event.target.value = '';
  }

  removeDocument(index: number): void {
    this.selectedDocuments.splice(index, 1);
    this.updateNomFichier();
  }

  updateNomFichier(): void {
    if (this.selectedDocuments.length > 0) {
      this.rapport.nomFichier = this.selectedDocuments
        .map((f) => f.name)
        .join(', ');

      this.rapport.cheminFichier = this.selectedDocuments
        .map((f) => `/uploads/documents/${f.name}`)
        .join(', ');

      this.rapport.fileSize = this.selectedDocuments.reduce(
        (total, f) => total + f.size,
        0
      );
    } else {
      this.rapport.nomFichier = '';
      this.rapport.cheminFichier = '';
      this.rapport.fileSize = 0;
    }
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) {
      return '0 B';
    }

    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k,));

    return (
      parseFloat(
        (bytes / Math.pow(k, i)).toFixed(2)
      ) +
      ' ' +
      sizes[i]
    );
  }

  enregistrerRapport(): void {
    if (!this.rapport.titre) {
      this.errorMessage = 'Le titre du rapport est obligatoire.';
      return;
    }

    if (!this.rapport.auteurNom) {
      this.rapport.auteurNom = 'Secrétariat';
    }

    this.loading = true;
    this.errorMessage = '';

    this.rapportService.createRapport(this.rapport).subscribe({
      next: (created) => {
        this.loading = false;
        this.successMessage =
          'Rapport créé et soumis avec succès !';

        setTimeout(() => {
          this.router.navigate(['/pages/rapports']);
        }, 1500);
      },

      error: (err) => {
        this.loading = false;

        const msg = err?.error?.message || err?.message;

        this.errorMessage = msg
          ? `Erreur : ${msg}`
          : 'Erreur lors de la création du rapport. Veuillez vérifier la connexion backend.';
      },
    });
  }

  annuler(): void {
    this.router.navigate(['/pages/rapports']);
  }
}
