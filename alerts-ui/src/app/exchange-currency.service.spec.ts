import { TestBed, inject } from '@angular/core/testing';

import { ExchangeCurrencyService } from './exchange-currency.service';

describe('ExchangeCurrencyService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ExchangeCurrencyService]
    });
  });

  it('should be created', inject([ExchangeCurrencyService], (service: ExchangeCurrencyService) => {
    expect(service).toBeTruthy();
  }));
});
