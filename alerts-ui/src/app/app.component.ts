import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';

import 'rxjs/add/operator/distinctUntilChanged';

import { TranslateService } from '@ngx-translate/core';

import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Crypto Coin Alerts';

  constructor(
      translate: TranslateService,
      private router: Router) {

    translate.setDefaultLang('en');

    // TODO: choose lang based on the user preferences
    translate.use('en');

    // define langs
    translate.setTranslation('en', this.englishLang());
  }

  ngOnInit() {
    // integrate google analytics via gtag - based on https://stackoverflow.com/a/47658214/3211175
    this.router.events.distinctUntilChanged((previous: any, current: any) => {
      // Subscribe to any `NavigationEnd` events where the url has changed
      if (current instanceof NavigationEnd) {
        return previous.url === current.url;
      }

      return true;
    }).subscribe((x: any) => {
      (<any>window).gtag('config', environment.gtag.id, { 'page_path': x.url });
    });
  }

  englishLang(): Object {
    return {
      'home.descripion': 'Crypto Coin Alerts let you be aware of the changes in your crypto currencies',
      'newCurrencyAlerts.description': 'Get notified when a new currency is added to your exchanges, we check it every hour!',
      // default messages from angular
      'required': 'a value is required',
      'email': 'invalid email',
      'minlength': 'more characters are required',
      'maxlength': 'too many characters',
      'min': 'the value is too small',
      'max': 'the value is too big',
      // labels
      'label.sort': 'Sort by',
      'label.reverseOrder': 'Reverse order',
      'label.verifyingEmail': 'Verifying email',
      'label.login': 'Log in',
      'label.logout': 'Log out',
      'label.newAccount': 'New account',
      'label.fixedPriceAlerts': 'Fixed price alerts',
      'label.newCurrencyAlerts': 'New currency alerts',
      'label.display': 'Display',
      // messages
      'message.welcome': 'Welcome',
      'message.bye': 'You are now logged out',
      'message.authError': 'You are not authorized, please log in',
      'message.verifyEmail': 'An email has been sent to you inbox, please click on the link to verify your email',
      'message.emailVerified': 'Thanks for verifying your email',
      'message.resolveCaptcha': 'Resolve the reCAPTCHA',
      'message.alertCreated': 'Your alert was created',
      'message.confirmDeleteAlert': 'The alert is going to be deleted, continue?',
      'message.alertDeleted': 'Alert deleted',
      'message.yourAboveFixedPriceAlert': 'You will be notified when {{currency}} is above {{price}} {{market}} on {{exchange}}',
      'message.yourBelowFixedPriceAlert': 'You will be notified when {{currency}} is below {{price}} {{market}} on {{exchange}}',
      'message.newCurrencyAlertCreated': 'Alert created for {{exchange}}',
      'message.newCurrencyAlertDeleted': 'Alert deleted for {{exchange}}',
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
      'field.createdOn': 'Created on',
      'field.triggeredOn': 'Triggered on',
      'field.pendingAlerts': 'Pending alerts',
      'field.triggeredAlerts': 'Triggered alerts',
      'field.all': 'All',
      // actions
      'action.createAccount': 'Create account',
      'action.login': 'Log in',
      'action.createAlert': 'Create alert',
      'action.newAlert': 'New alert',
      'action.cancel': 'Cancel',
      'action.delete': 'Delete',
      // custom validations
      'validation.passwordMismatch': 'the password does not match'
    };
  }
}
