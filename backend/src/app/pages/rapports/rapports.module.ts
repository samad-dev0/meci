import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  NbCardModule,
  NbButtonModule,
  NbInputModule,
  NbSelectModule,
  NbIconModule,
  NbBadgeModule,
  NbUserModule,
  NbCheckboxModule,
} from '@nebular/theme';

import { RapportsRoutingModule } from './rapports-routing.module';
import { RapportListComponent } from './rapport-list/rapport-list.component';
import { RapportCreateComponent } from './rapport-create/rapport-create.component';

@NgModule({
  declarations: [
    RapportListComponent,
    RapportCreateComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RapportsRoutingModule,
    NbCardModule,
    NbButtonModule,
    NbInputModule,
    NbSelectModule,
    NbIconModule,
    NbBadgeModule,
    NbUserModule,
    NbCheckboxModule,
  ],
})
export class RapportsModule {}
