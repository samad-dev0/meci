import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MENU_ITEMS } from './pages-menu';
import { AccessRightsService } from '../auth/services/access-rights.service';
import { NbMenuItem } from '@nebular/theme';
import { EMPTY, Subscription, timer } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

@Component({
  selector: 'ngx-pages',
  styleUrls: ['pages.component.scss'],
  template: `
    <ngx-one-column-layout>
      <nb-menu [items]="menu"></nb-menu>
      <router-outlet></router-outlet>
    </ngx-one-column-layout>
  `,
})
export class PagesComponent implements OnInit, OnDestroy {
  menu: NbMenuItem[] = [];
  private rightsSubscription?: Subscription;
  private refreshSubscription?: Subscription;

  constructor(
    private accessRights: AccessRightsService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.rightsSubscription = this.accessRights.rights$.subscribe(() => {
      this.menu = this.accessRights.filterMenu(MENU_ITEMS);
    });
    this.accessRights.ensureLoaded().subscribe({
      next: () => this.redirectIfAccessWasRemoved(),
      error: (error) => console.error('Impossible de charger les droits d’accès.', error),
    });
    this.refreshSubscription = timer(5000, 5000).pipe(
      switchMap(() => this.accessRights.getMyRights().pipe(
        catchError((error) => {
          console.error('Impossible de rafraîchir les droits d’accès.', error);
          return EMPTY;
        }),
      )),
    ).subscribe(() => this.redirectIfAccessWasRemoved());
  }

  ngOnDestroy(): void {
    this.rightsSubscription?.unsubscribe();
    this.refreshSubscription?.unsubscribe();
  }

  private redirectIfAccessWasRemoved(): void {
    const currentUrl = this.router.url;
    if (currentUrl.startsWith('/pages/')
      && !currentUrl.startsWith('/pages/no-access')
      && !this.accessRights.canOpenRoute(currentUrl)) {
      this.router.navigateByUrl('/pages/no-access');
    }
  }
}
