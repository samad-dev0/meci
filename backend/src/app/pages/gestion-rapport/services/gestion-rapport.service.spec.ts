import { TestBed } from '@angular/core/testing';

import { GestionRapportService } from './gestion-rapport.service';

describe('GestionRapportService', () => {
  let service: GestionRapportService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GestionRapportService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
