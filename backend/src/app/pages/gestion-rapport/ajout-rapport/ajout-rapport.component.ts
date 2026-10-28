import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';
import { RapportService } from '../../rapports/services/rapport.service';
import { Rapport, TypeRapport } from '../../rapports/models/rapport.model';

@Component({
  selector: 'ngx-ajout-rapport',
  templateUrl: './ajout-rapport.component.html',
  styleUrls: ['./ajout-rapport.component.scss']
})
export class AjoutRapportComponent implements OnInit {
  titre = '';
  typeRapport: TypeRapport | '' = '';
  methodeSaisie: 'import' | 'direct' = 'import';
  contenuRapport = '';
  fichierSelectionne: File | null = null;
  chargement = false;
  messageErreur = '';

  constructor(
    private readonly router: Router,
    private readonly rapportService: RapportService,
    private readonly authService: AuthService,
  ) { }

  ngOnInit(): void {
    if (!this.authService.getUtilisateurConnecte()) {
      this.messageErreur = 'Connectez-vous pour ajouter un rapport. Il restera visible uniquement par vous, jusqu’à une autorisation ciblée.';
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files[0];
    this.fichierSelectionne = file || null;
  }

  soumettreRapport(): void {
    if (!this.titre.trim()) {
      this.messageErreur = 'Le titre est obligatoire.';
      return;
    }
    const typeRapport = this.typeRapport;
    if (!typeRapport) {
      this.messageErreur = 'Veuillez choisir une catégorie de rapport.';
      return;
    }
    if (!this.authService.getUtilisateurConnecte()) {
      this.messageErreur = 'Connectez-vous pour enregistrer un rapport.';
      return;
    }

    this.chargement = true;
    this.messageErreur = '';

    if (this.methodeSaisie === 'import') {
      if (!this.fichierSelectionne) {
        this.messageErreur = 'Veuillez sélectionner un fichier.';
        this.chargement = false;
        return;
      }
      this.rapportService.creerRapportAvecFichier(this.titre.trim(), this.fichierSelectionne, typeRapport).subscribe({
        next: () => this.router.navigate(['/pages/gestion-rapport/liste']),
        error: err => {
          this.messageErreur = err?.error?.message || 'Impossible d’enregistrer le rapport.';
          this.chargement = false;
        },
      });
      return;
    }

    if (!this.contenuRapport.trim()) {
      this.messageErreur = 'Le contenu ne peut pas être vide.';
      this.chargement = false;
      return;
    }

    const rapport: Rapport = {
      titre: this.titre.trim(),
      typeRapport,
      auteurNom: '',
      contenu: this.contenuRapport,
      fileSize: new Blob([this.contenuRapport]).size,
    };

    this.rapportService.createRapport(rapport).subscribe({
      next: () => this.router.navigate(['/pages/gestion-rapport/liste']),
      error: err => {
        this.messageErreur = err?.error?.message || 'Impossible d’enregistrer le rapport.';
        this.chargement = false;
      },
    });
  }
}
