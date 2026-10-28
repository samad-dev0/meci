import { Component, EventEmitter, Output } from '@angular/core';
import { AdminAuthService } from '../services/admin-auth.service';

@Component({
  selector: 'ngx-admin-login',
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.scss'],
})
export class AdminLoginComponent {
  nomUtilisateur = '';
  motDePasse = '';
  errorMessage = '';
  loading = false;

  @Output() loginSuccess = new EventEmitter<void>();

  constructor(private adminAuthService: AdminAuthService) {}

  seConnecter(): void {
    if (!this.nomUtilisateur || !this.motDePasse) {
      this.errorMessage = 'Veuillez saisir votre nom et votre mot de passe pour accéder à la page administrateur.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.adminAuthService.login({
      nomUtilisateur: this.nomUtilisateur,
      motDePasse: this.motDePasse,
    }).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.loginSuccess.emit();
        } else {
          this.errorMessage = res.message || 'Identifiants d\'administration invalides.';
        }
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Erreur lors de la connexion. Veuillez réessayer.';
      },
    });
  }
}
