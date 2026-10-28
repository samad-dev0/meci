import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RapportListComponent } from './rapport-list/rapport-list.component';
import { RapportCreateComponent } from './rapport-create/rapport-create.component';

const routes: Routes = [
  {
    path: '',
    component: RapportListComponent,
  },
  {
    path: 'nouveau',
    component: RapportCreateComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RapportsRoutingModule {}
