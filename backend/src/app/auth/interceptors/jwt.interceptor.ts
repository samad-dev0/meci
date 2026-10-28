import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { environment } from '../../../environments/environment';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (!request.url.startsWith(environment.apiUrl)) {
      return next.handle(request);
    }

    if (request.url.includes('/auth/login')) {
      return next.handle(request);
    }

    const isAdminRequest = request.url.startsWith(`${environment.apiUrl}/admin/`);
    const isUserManagementRequest = request.url.startsWith(`${environment.apiUrl}/utilisateurs`);
    const adminToken = localStorage.getItem('meci_admin_token');
    const userToken = this.authService.getToken();

    let token = this.getUsableToken(userToken);
    if (isAdminRequest || isUserManagementRequest) {
      const usableAdminToken = this.getUsableToken(adminToken);
      if (usableAdminToken) {
        token = usableAdminToken;
      }
    }

    if (!token) {
      return next.handle(request);
    }

    return next.handle(request.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    }));
  }

  private getUsableToken(token: string | null): string | null {
    if (!token) {
      return null;
    }

    try {
      const encodedPayload = token.split('.')[1]
        .replace(/-/g, '+')
        .replace(/_/g, '/');
      const payload = JSON.parse(atob(encodedPayload.padEnd(
        Math.ceil(encodedPayload.length / 4) * 4,
        '='
      )));
      return typeof payload.exp === 'number' && payload.exp * 1000 > Date.now()
        ? token
        : null;
    } catch {
      return null;
    }
  }
}
