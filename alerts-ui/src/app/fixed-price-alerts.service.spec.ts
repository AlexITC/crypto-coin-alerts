import { TestBed, inject } from '@angular/core/testing';

import { FixedPriceAlertsService } from './fixed-price-alerts.service';

describe('FixedPriceAlertsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FixedPriceAlertsService]
    });
  });

  it('should be created', inject([FixedPriceAlertsService], (service: FixedPriceAlertsService) => {
    expect(service).toBeTruthy();
  }));
});
