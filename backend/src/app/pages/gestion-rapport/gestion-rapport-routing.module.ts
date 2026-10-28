import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { GestionRapportComponent } from './gestion-rapport.component';
import { AjoutRapportComponent } from './ajout-rapport/ajout-rapport.component';
import { ListeRapportComponent } from './liste-rapport/liste-rapport.component';
import { RapportsPartagesComponent } from './rapports-partages/rapports-partages.component';

const routes: Routes = [{
  path: '',
  component: GestionRapportComponent,
  children: [
    {
      path: 'ajout',
      component: AjoutRapportComponent,
    },
    {
      path: 'liste',
      component: ListeRapportComponent,
    },
    {
      path: 'partages',
      component: RapportsPartagesComponent,
    },
    {
      path: '',
      redirectTo: 'liste',
      pathMatch: 'full',
    },
  ],
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GestionRapportRoutingModule { }
