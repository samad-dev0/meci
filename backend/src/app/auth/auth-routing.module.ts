import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './login/login.component';
import { ResetPasswordComponent } from './reset-password/reset-password/reset-password.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password/forgot-password.component';

const routes: Routes = [

{
path: 'login',
component: LoginComponent
},

{
  path: 'forgot-password',
  component: ForgotPasswordComponent
},
{
  path: 'reset-password',
  component: ResetPasswordComponent
}


];

@NgModule({
imports: [
RouterModule.forChild(routes)
],

exports: [
RouterModule
]
})
export class AuthRoutingModule {}
