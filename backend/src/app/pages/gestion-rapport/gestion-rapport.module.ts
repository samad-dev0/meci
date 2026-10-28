import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GestionRapportRoutingModule } from './gestion-rapport-routing.module';
import { GestionRapportComponent } from './gestion-rapport.component';
import { AjoutRapportComponent } from './ajout-rapport/ajout-rapport.component';
import { ListeRapportComponent } from './liste-rapport/liste-rapport.component';
import { RapportsPartagesComponent } from './rapports-partages/rapports-partages.component';


import {
  NbCardModule,
  NbIconModule,
  NbInputModule,
  NbTreeGridModule,
  NbButtonModule,
  NbRadioModule,
  NbDatepickerModule
} from '@nebular/theme';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AccessRightsModule } from '../../auth/directives/access-rights.module';

@NgModule({
  declarations: [
    GestionRapportComponent,
    AjoutRapportComponent,
    ListeRapportComponent,
    RapportsPartagesComponent
  ],
  imports: [
    CommonModule,
    AccessRightsModule,
    GestionRapportRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    NbCardModule,
    NbIconModule,
    NbInputModule,
    NbButtonModule,
    NbRadioModule,
    NbDatepickerModule,
    NbTreeGridModule,
  ]
})
export class GestionRapportModule { }
