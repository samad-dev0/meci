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
  NbCheckboxModule,
} from '@nebular/theme';

import { AdminRapportRoutingModule } from './admin-rapport-routing.module';
import { AdminRapportComponent } from './admin-rapport.component';
import { AdminLoginComponent } from './admin-login/admin-login.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';

@NgModule({
  declarations: [
    AdminRapportComponent,
    AdminLoginComponent,
    AdminDashboardComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AdminRapportRoutingModule,
    NbCardModule,
    NbButtonModule,
    NbInputModule,
    NbSelectModule,
    NbIconModule,
    NbBadgeModule,
    NbCheckboxModule,
  ],
})
export class AdminRapportModule {}
