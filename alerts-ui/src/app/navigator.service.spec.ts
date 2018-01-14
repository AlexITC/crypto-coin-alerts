import { TestBed, inject } from '@angular/core/testing';

import { NavigatorService } from './navigator.service';

describe('NavigatorService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NavigatorService]
    });
  });

  it('should be created', inject([NavigatorService], (service: NavigatorService) => {
    expect(service).toBeTruthy();
  }));
});
