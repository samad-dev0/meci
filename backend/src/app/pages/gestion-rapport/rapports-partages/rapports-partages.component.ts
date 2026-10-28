import { Component, OnInit } from '@angular/core';
import { Rapport } from '../../rapports/models/rapport.model';
import { RapportService } from '../../rapports/services/rapport.service';

@Component({
  selector: 'ngx-rapports-partages',
  templateUrl: './rapports-partages.component.html',
})
export class RapportsPartagesComponent implements OnInit {
  rapports: Rapport[] = [];
  rapportSelectionne: Rapport | null = null;
  chargement = false;
  messageErreur = '';

  constructor(private rapportService: RapportService) {}

  ngOnInit(): void {
    this.chargement = true;
    this.rapportService.getRapportsPartages().subscribe({
      next: rapports => {
        this.rapports = rapports;
        this.chargement = false;
      },
      error: () => {
        this.messageErreur = 'Impossible de charger les rapports partagés.';
        this.chargement = false;
      },
    });
  }

  voirRapport(rapport: Rapport): void {
    this.rapportSelectionne = rapport;
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

  fermerRapport(): void {
    this.rapportSelectionne = null;
  }
}
