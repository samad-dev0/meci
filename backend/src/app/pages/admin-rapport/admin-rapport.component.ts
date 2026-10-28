import { Component, OnInit } from '@angular/core';
import { AdminAuthService } from './services/admin-auth.service';

@Component({
  selector: 'ngx-admin-rapport',
  template: `
    <ngx-admin-login *ngIf="!isLoggedIn" (loginSuccess)="onLoginSuccess()"></ngx-admin-login>
    <ngx-admin-dashboard *ngIf="isLoggedIn"></ngx-admin-dashboard>
  `,
})
export class AdminRapportComponent implements OnInit {
  isLoggedIn = false;

  constructor(private adminAuthService: AdminAuthService) {}

  ngOnInit(): void {
    this.adminAuthService.isLoggedIn$.subscribe((loggedIn) => {
      this.isLoggedIn = loggedIn;
    });
  }

  onLoginSuccess(): void {
    this.isLoggedIn = true;
  }
}
