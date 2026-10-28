import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateChild,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { AccessRightsService } from '../services/access-rights.service';

@Injectable({
  providedIn: 'root',
})
export class PermissionGuard implements CanActivateChild {
  constructor(
    private accessRights: AccessRightsService,
    private router: Router,
  ) {}

  canActivateChild(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<boolean | UrlTree> {
    if (state.url.startsWith('/pages/no-access')) {
      return of(true);
    }

    return this.accessRights.ensureLoaded().pipe(
      map(() => this.accessRights.canOpenRoute(state.url)
        ? true
        : this.router.parseUrl('/pages/no-access')),
    );
  }
}
