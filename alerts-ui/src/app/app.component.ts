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
