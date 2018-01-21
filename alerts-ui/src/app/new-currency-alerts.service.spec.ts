import { TestBed, inject } from '@angular/core/testing';

import { NewCurrencyAlertsService } from './new-currency-alerts.service';

describe('NewCurrencyAlertsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NewCurrencyAlertsService]
    });
  });

  it('should be created', inject([NewCurrencyAlertsService], (service: NewCurrencyAlertsService) => {
    expect(service).toBeTruthy();
  }));
});
