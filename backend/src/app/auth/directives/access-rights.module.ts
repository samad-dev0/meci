import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CanAccessDirective } from './can-access.directive';

@NgModule({
  imports: [CommonModule],
  declarations: [CanAccessDirective],
  exports: [CanAccessDirective],
})
export class AccessRightsModule {}
