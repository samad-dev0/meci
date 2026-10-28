import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionRapportComponent } from './gestion-rapport.component';

describe('GestionRapportComponent', () => {
  let component: GestionRapportComponent;
  let fixture: ComponentFixture<GestionRapportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GestionRapportComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionRapportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
