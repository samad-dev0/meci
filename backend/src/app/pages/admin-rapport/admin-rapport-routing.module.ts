import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminRapportComponent } from './admin-rapport.component';

const routes: Routes = [
  {
    path: '',
    component: AdminRapportComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRapportRoutingModule {}
