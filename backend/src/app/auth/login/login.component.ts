import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../services/auth.service';

@Component({
  selector: 'ngx-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  loginForm: FormGroup;
  chargement = false;
  erreur = '';
  afficherMotDePasse = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    if (this.authService.estConnecte()) {
      this.router.navigateByUrl('/pages/dashboard');
    }
  }

  seConnecter(): void {
    this.erreur = '';

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.chargement = true;

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.chargement = false;
        this.router.navigate(['/pages/dashboard']);
      },
      error: (error) => {
        this.chargement = false;

        if (error.status === 401) {
          this.erreur = 'Adresse e-mail ou mot de passe incorrect.';
        } else if (error.error?.message) {
          this.erreur = error.error.message;
        } else {
          this.erreur = 'Impossible de se connecter au serveur.';
        }
      },
    });
  }

  toggleMotDePasse(): void {
    this.afficherMotDePasse = !this.afficherMotDePasse;
  }

  motDePasseOublie(): void {
    this.router.navigate(['/auth/forgot-password']);
  }
}

