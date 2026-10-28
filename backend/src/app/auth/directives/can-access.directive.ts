import {
  Directive,
  Input,
  OnDestroy,
  OnInit,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';
import { Subscription } from 'rxjs';
import { AccessRightsService } from '../services/access-rights.service';

@Directive({
  selector: '[appCanAccess]',
})
export class CanAccessDirective implements OnInit, OnDestroy {
  @Input('appCanAccess') permissionCode = '';

  private subscription?: Subscription;
  private visible = false;

  constructor(
    private templateRef: TemplateRef<unknown>,
    private viewContainer: ViewContainerRef,
    private accessRights: AccessRightsService,
  ) {}

  ngOnInit(): void {
    this.subscription = this.accessRights.rights$.subscribe(() => this.updateVisibility());
    this.updateVisibility();
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  private updateVisibility(): void {
    const shouldBeVisible = this.accessRights.can(this.permissionCode);
    if (shouldBeVisible === this.visible) {
      return;
    }
    this.visible = shouldBeVisible;
    if (shouldBeVisible) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }
}
