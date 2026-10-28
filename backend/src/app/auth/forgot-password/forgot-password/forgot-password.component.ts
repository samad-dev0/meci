import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'ngx-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {

  formulaire: FormGroup;

  chargement = false;
  succes = false;
  erreur = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.formulaire = this.fb.group({
      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ]
    });
  }

  envoyerDemande(): void {

    this.erreur = '';
    this.succes = false;

    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }

    this.chargement = true;

    const email = this.formulaire.value.email;

    this.authService
      .demanderReinitialisation(email)
      .subscribe({
        next: (response) => {

          console.log(
            'Demande de réinitialisation envoyée :',
            response
          );

          this.chargement = false;
          this.succes = true;
        },

        error: (error) => {

          console.error(
            'Erreur réinitialisation :',
            error
          );

          this.chargement = false;

          this.erreur =
            error?.error?.message ||
            'Une erreur est survenue. Veuillez réessayer.';
        }
      });
  }

  retournerConnexion(): void {
    this.router.navigate(['/auth/login']);
  }
}
