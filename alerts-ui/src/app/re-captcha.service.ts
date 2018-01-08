import { Injectable } from '@angular/core';

import { environment } from '../environments/environment';

@Injectable()
export class ReCaptchaService {

  constructor() { }

  siteKey: string = environment.recaptcha.siteKey;

}
