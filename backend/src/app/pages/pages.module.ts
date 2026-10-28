import { NgModule } from '@angular/core';
import { NbMenuModule } from '@nebular/theme';

import { ThemeModule } from '../@theme/theme.module';
import { PagesComponent } from './pages.component';
import { DashboardModule } from './dashboard/dashboard.module';
import { ECommerceModule } from './e-commerce/e-commerce.module';
import { PagesRoutingModule } from './pages-routing.module';
import { MiscellaneousModule } from './miscellaneous/miscellaneous.module';
import { AdministrationModule } from './administration/administration.module';
import { AccessRightsModule } from '../auth/directives/access-rights.module';
import { NoAccessComponent } from './no-access.component';

@NgModule({
  imports: [
    PagesRoutingModule,
    ThemeModule,
    NbMenuModule,
    DashboardModule,
    ECommerceModule,
    MiscellaneousModule,
    AdministrationModule,
    AccessRightsModule,
  ],
  declarations: [
    PagesComponent,
    NoAccessComponent,
  ],
})
export class PagesModule {
}
