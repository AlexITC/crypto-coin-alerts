import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Crypto Coin Alerts';

  constructor(translate: TranslateService) {
    translate.setDefaultLang('en');

    // TODO: choose lang based on the user preferences
    translate.use('en');

    // define langs
    translate.setTranslation('en', this.englishLang());
  }

  englishLang(): Object {
    return {
      // default messages from angular
      'required': 'a value is required',
      'email': 'invalid email',
      'minlength': 'more characters are required',
      'maxlength': 'too many characters',
      'min': 'the value is too small',
      'max': 'the value is too big',
      // labels
      'label.verifyingEmail': 'Verifying email',
      // messages
      'message.welcome': 'Welcome',
      'message.verifyEmail': 'An email has been sent to you inbox, please click on the link to verify your email',
      'message.emailVerified': 'Thanks for verifying your email',
      'message.resolveCaptcha': 'Resolve the reCAPTCHA',
      'message.alertCreated': 'Your alert was created',
      // field names
      'field.email': 'Email',
      'field.password': 'Password',
      'field.repeatPassword': 'Repeat password',
      'field.exchange': 'Exchange',
      'field.market': 'Market',
      'field.currency': 'Currency',
      'field.priceCondition': 'Price condition',
      'field.above': 'Above',
      'field.below': 'Below',
      'field.price': 'Price',
      'field.basePrice': 'The price when you bought / sold',
      'field.optional': 'optional',
      // actions
      'action.createAccount': 'Create account',
      'action.login': 'Log in',
      'action.createAlert': 'Create alert',
      'action.newAlert': 'New alert',
      'action.cancel': 'Cancel',
      // custom validations
      'validation.passwordMismatch': 'the password does not match'
    };
  }
}
