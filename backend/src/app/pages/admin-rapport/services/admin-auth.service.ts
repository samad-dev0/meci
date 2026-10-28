import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';

export interface AdminLoginRequest {
  nomUtilisateur: string;
  motDePasse: string;
}

export interface AdminLoginResponse {
  success: boolean;
  message: string;
  token?: string;
  username?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AdminAuthService {
  private apiUrl = `${environment.apiUrl}/admin/auth`;
  private loggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isLoggedIn$ = this.loggedInSubject.asObservable();

  constructor(private http: HttpClient) {}

  private hasToken(): boolean {
    return !!localStorage.getItem('meci_admin_token');
  }

  get currentUser(): string | null {
    return localStorage.getItem('meci_admin_username') || 'Administrateur';
  }

  login(credentials: AdminLoginRequest): Observable<AdminLoginResponse> {
    return this.http.post<AdminLoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((res) => {
        if (res.success && res.token) {
          localStorage.setItem('meci_admin_token', res.token);
          localStorage.setItem('meci_admin_username', res.username || credentials.nomUtilisateur);
          this.loggedInSubject.next(true);
        }
      }),
      catchError((err) => {
        this.logout();
        return of({
          success: false,
          message: err.error?.message || 'Connexion impossible. Vérifiez vos identifiants et réessayez.',
        });
      })
    );
  }

  logout(): void {
    localStorage.removeItem('meci_admin_token');
    localStorage.removeItem('meci_admin_username');
    this.loggedInSubject.next(false);
  }

  isAuthenticated(): boolean {
    return this.loggedInSubject.value;
  }
}
