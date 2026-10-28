import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AjoutRapportComponent } from './ajout-rapport.component';

describe('AjoutRapportComponent', () => {
  let component: AjoutRapportComponent;
  let fixture: ComponentFixture<AjoutRapportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AjoutRapportComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AjoutRapportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
