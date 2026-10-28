import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NbIconModule } from '@nebular/theme';
import { AccessRightsModule } from '../../auth/directives/access-rights.module';

import { ThemeModule } from '../../@theme/theme.module';
import { AdministrationRoutingModule } from './administration-routing.module';
import { UsersComponent } from './users/users.component';
import { AutoriseRapportComponent } from './autorise-rapport/autorise-rapport.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ThemeModule,
    NbIconModule,
    AccessRightsModule,
    AdministrationRoutingModule,
  ],
  declarations: [
    UsersComponent,
    AutoriseRapportComponent,
  ],
})
export class AdministrationModule {}
