import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'ngx-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  formulaire: FormGroup;

  token = '';

  chargement = false;
  succes = false;
  erreur = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {

    this.formulaire = this.fb.group(
      {
        password: [
          '',
          [
            Validators.required,
            Validators.minLength(8)
          ]
        ],

        confirmationPassword: [
          '',
          [
            Validators.required
          ]
        ]
      },
      {
        validators: this.motsDePasseIdentiques
      }
    );
  }

  ngOnInit(): void {

    this.route.queryParams.subscribe(params => {

      this.token = params['token'] || '';

      console.log(
        'Token de réinitialisation reçu :',
        this.token ? 'présent' : 'absent'
      );

      if (!this.token) {
        this.erreur =
          'Le lien de réinitialisation est invalide ou incomplet.';
      }
    });
  }

  motsDePasseIdentiques(
    control: AbstractControl
  ): ValidationErrors | null {

    const password =
      control.get('password')?.value;

    const confirmation =
      control.get('confirmationPassword')?.value;

    if (
      password &&
      confirmation &&
      password !== confirmation
    ) {
      return {
        motsDePasseDifferents: true
      };
    }

    return null;
  }

  reinitialiser(): void {
    this.erreur = '';

    if (!this.token) {
      this.erreur = 'Le lien de réinitialisation est invalide.';
      return;
    }

    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }

    this.chargement = true;

    const password = this.formulaire.get('password')?.value;

    console.log('Token envoyé :', this.token);
    console.log('Nouveau mot de passe envoyé :', '********');

    this.authService
      .reinitialiserMotDePasse(this.token, password)
      .subscribe({
        next: (response) => {
          console.log('Réinitialisation réussie :', response);

          this.chargement = false;
          this.succes = true;
        },

        error: (error) => {
          console.error('Erreur réinitialisation :', error);

          this.chargement = false;

          this.erreur =
            error?.error?.message ||
            'Le lien est invalide ou a expiré.';
        }
      });
  }

  retournerConnexion(): void {
    this.router.navigate(['/auth/login']);
  }
}
