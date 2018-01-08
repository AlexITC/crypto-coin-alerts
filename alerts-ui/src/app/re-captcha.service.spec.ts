import { TestBed, inject } from '@angular/core/testing';

import { ReCaptchaService } from './re-captcha.service';

describe('ReCaptchaService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ReCaptchaService]
    });
  });

  it('should be created', inject([ReCaptchaService], (service: ReCaptchaService) => {
    expect(service).toBeTruthy();
  }));
});
