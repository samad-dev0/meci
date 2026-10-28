import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NbMenuItem } from '@nebular/theme';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { finalize, map, shareReplay, tap } from 'rxjs/operators';
import { MENU_ITEMS } from '../../pages/pages-menu';

export interface AccessRightNode {
  code: string;
  label: string;
  type: 'GROUP' | 'MENU' | 'PAGE' | 'ACTION';
  route: string | null;
  children: AccessRightNode[];
  selected?: boolean;
  checked?: boolean;
  indeterminate?: boolean;
  expanded?: boolean;
}

export interface AccessRightsResponse {
  codes: string[];
  superAdmin: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class AccessRightsService {
  private readonly apiUrl = 'http://localhost:8080/api/access-control';
  private readonly rightsSubject = new BehaviorSubject<AccessRightsResponse>({
    codes: [],
    superAdmin: false,
  });
  readonly rights$ = this.rightsSubject.asObservable();

  private loaded = false;
  private loading?: Observable<void>;

  constructor(private http: HttpClient) {}

  getCatalog(): Observable<AccessRightNode[]> {
    return this.http.get<AccessRightNode[]>(`${this.apiUrl}/catalog`);
  }

  reset(): void {
    this.loaded = false;
    this.loading = undefined;
    this.rightsSubject.next({ codes: [], superAdmin: false });
  }

  getMyRights(): Observable<void> {
    return this.http.get<AccessRightsResponse>(`${this.apiUrl}/me`).pipe(
      tap((rights) => {
        this.rightsSubject.next(rights);
        this.loaded = true;
      }),
      map(() => undefined),
    );
  }

  ensureLoaded(): Observable<void> {
    if (this.loaded) {
      return of(undefined);
    }
    if (!this.loading) {
      this.loading = this.getMyRights().pipe(
        shareReplay(1),
        finalize(() => {
          this.loading = undefined;
        }),
      );
    }
    return this.loading;
  }

  can(code: string): boolean {
    const rights = this.rightsSubject.value;
    return rights.superAdmin || rights.codes.includes(code);
  }

  isSuperAdmin(): boolean {
    return this.rightsSubject.value.superAdmin;
  }

  canViewPage(code: string): boolean {
    const rights = this.rightsSubject.value;
    return rights.superAdmin
      || rights.codes.some((right) => right === code || right.startsWith(`${code}.`));
  }

  canOpenRoute(url: string): boolean {
    if (this.isSuperAdmin()) {
      return true;
    }
    const path = url.split(/[?#]/, 1)[0].replace(/\/$/, '');
    const item = this.findMenuItem(MENU_ITEMS, path);
    const code = item?.data?.permissionCode;
    return typeof code === 'string' && this.canViewPage(code);
  }

  filterMenu(items: NbMenuItem[]): NbMenuItem[] {
    const visible: NbMenuItem[] = [];
    let pendingGroup: NbMenuItem | undefined;

    items.forEach((item) => {
      if (item.group && !item.children?.length) {
        pendingGroup = item;
        return;
      }

      const children = item.children ? this.filterMenu(item.children) : undefined;
      const code = item.data?.permissionCode as string | undefined;
      const selfVisible = !!code && (item.link ? this.canViewPage(code) : this.can(code));
      if (item.group ? !!children?.length : selfVisible || !!children?.length) {
        const visibleItem = { ...item, children };
        if (!selfVisible && children?.length) {
          visibleItem.link = undefined;
          visibleItem.home = false;
        }
        if (pendingGroup) {
          visible.push(pendingGroup);
          pendingGroup = undefined;
        }
        visible.push(visibleItem);
      }
    });

    return visible;
  }

  private findMenuItem(items: NbMenuItem[], path: string): NbMenuItem | undefined {
    return items.reduce<NbMenuItem | undefined>((match, item) => {
      if (match) {
        return match;
      }
      const link = item.link?.replace(/\/$/, '');
      if (link === path || (item.pathMatch === 'prefix' && path.startsWith(`${link}/`))) {
        return item;
      }
      return item.children ? this.findMenuItem(item.children, path) : undefined;
    }, undefined);
  }
}
