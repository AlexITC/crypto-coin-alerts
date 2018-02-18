import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';

import 'rxjs/add/operator/distinctUntilChanged';

import { TranslateService } from '@ngx-translate/core';

import { environment } from '../environments/environment';

import { DEFAULT_LANG, LanguageService } from './language.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Crypto Coin Alerts';

  constructor(
      private translate: TranslateService,
      private languageService: LanguageService,
      private router: Router) {

    translate.setDefaultLang(DEFAULT_LANG);
    translate.use(languageService.getLang());

    // define langs
    translate.setTranslation('en', this.englishLang());
    translate.setTranslation('es', this.spanishLang());
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
      const dirtyUrl: string = x.url || '';
      const url = this.cleanUrlForAnalytics(dirtyUrl);
      (<any>window).gtag('config', environment.gtag.id, { 'page_path': url });
    });
  }

  private cleanUrlForAnalytics(dirtyUrl: string): string {
    const urlWithoutParams = this.removeQueryParams(dirtyUrl);
    const urlWithoutToken = this.removeTokens(urlWithoutParams);

    return urlWithoutToken;
  }

  private removeTokens(dirtyUrl: string): string {
    const urlWithToken = ['/verify-email'];
    const result = urlWithToken
      .filter(prefix => dirtyUrl.indexOf(prefix) >= 0)
      .map(prefix => dirtyUrl.substring(0, dirtyUrl.indexOf(prefix) + prefix.length) );

    if (result.length === 0) {
      return dirtyUrl;
    } else {
      return result[0];
    }
  }

  private removeQueryParams(url: string): string {
    const index = url.indexOf('?');
    if (index >= 0) {
      return url.substring(0, index);
    } else {
      return url;
    }
  }

  englishLang(): Object {
    return {
      'home.examples': 'Examples',
      'home.newCurrenciesAlertExample': 'New Currencies Alert',
      'home.priceAlertExample': 'Price Alert',
      'home.descripion': 'Crypto Coin Alerts let you be aware of the changes in your crypto currencies',
      'home.alertTypes': `<ul>
  <li>Get notified when a currency increases or decreases a given price</li>
  <li>Get notified when a new currency is added to the exchanges that you use</li>
</ul>`,

      'home.emailNotReceivedHelp': `If you have not received our email, please check your spam folder. <hr>
Still can't find it? Try searching Gmail for "in:all subject:(Confirm your account on Crypto Coin Alerts)" (without quotes). <hr>
Still can't find it? Just send us an email to support@cryptocoinalerts.net from the account that you registered and we'll validate it.`,

      'newCurrencyAlerts.description': 'Get notified when a new currency is added to your exchanges, we check it every hour!',

      'footer.description': 'Crypto Coin Alerts is an open source project, help us to improve it on ',
      'footer.stayUpdatedMessage': 'Stay updated following us on our social networks!',
      // default messages from angular
      'required': 'a value is required',
      'email': 'invalid email',
      'minlength': 'more characters are required',
      'maxlength': 'too many characters',
      'min': 'the value is too small',
      'max': 'the value is too big',
      // langs
      'en': 'English',
      'es': 'Spanish',
      // labels
      'label.language': 'Language',
      'label.sort': 'Sort by',
      'label.reverseOrder': 'Reverse order',
      'label.verifyingEmail': 'Verifying email',
      'label.login': 'Log in',
      'label.logout': 'Log out',
      'label.newAccount': 'New account',
      'label.fixedPriceAlerts': 'Fixed price alerts',
      'label.newCurrencyAlerts': 'New currency alerts',
      'label.display': 'Display',
      'label.alertTypes': 'Types of alerts that you can receive',
      'label.supportedExchanges': 'Supported exchanges',
      'label.emailNotReceived': 'Already registered? Haven\'t received the verification email?',
      'label.yourAlerts': 'Your alerts',
      'label.createFixedPriceAlert': 'Create a fixed price alert',
      // messages
      'message.serverUnavailable': 'Server unavailable, please try again in a minute',
      'message.welcome': 'Welcome',
      'message.bye': 'You are now logged out',
      'message.authError': 'You are not authorized, please log in',
      'message.verifyEmail': `Please check your email ({{email}}) to confirm your account. <hr>
If {{email}} is not your email address, please go back and enter the correct one. <hr>
If you have not received our email in 15 minutes, please check your spam folder. <hr>
Still can not find it? Try searching Gmail for "in:all subject:(Confirm your account on Crypto Coin Alerts)" (without quotes).`,

      'message.accountNeeded.question': 'What are you waiting? Create a',
      'message.accountNeeded.new': 'new account',
      'message.accountNeeded.or': 'or',
      'message.accountNeeded.login': 'login',
      'message.accountNeeded.last': 'to start receiving alerts',

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

  spanishLang(): Object {
    return {
      'home.examples': 'Ejemplos',
      'home.newCurrenciesAlertExample': 'Nuevas monedas',
      'home.priceAlertExample': 'Alerta por precio',
      'home.descripion': 'Crypto Coin Alerts te permite enterarte de los cambios en los precios de cripto monedas',
      'home.alertTypes': `<ul>
  <li>Recibe una alerta cuando una cripto moneda sube o baja de cierto precio</li>
  <li>Recibe una alerta cuando alguno de los exchanges que usas agrega una nueva moneda</li>
</ul>`,

      'home.emailNotReceivedHelp': `Si no has recibido el email para verificar tu cuenta,
por favor verifica en el correo no deseado (spam). <hr>
Si aún no lo encuentras? Intenta buscando esto en Gmail "in:all subject:(Confirm your account on Crypto Coin Alerts)" (sin comillas). <hr>
Si aún así, no lo encuentras? envianos un email a support@cryptocoinalerts.net desde la cuenta que registraste.`,

      'newCurrencyAlerts.description': 'Recibe una alerta cuando tus exchanges agregan una cripto moneda, ¡verificamos cada hora!',
      'footer.description': 'Crypto Coin Alerts es un proyecto open source, ayudanos a mejorarlo en ',
      'footer.stayUpdatedMessage': 'Mantente al tanto de las novedades siguiendonos en nuestras redes sociales',
      // default messages from angular
      'required': 'campo requerido',
      'email': 'email invalido',
      'minlength': 'se necesitan mas caracteres',
      'maxlength': 'demasiados caracteres',
      'min': 'el valor es muy pequeño',
      'max': 'el valor es muy grande',
      // langs
      'en': 'Inglés',
      'es': 'Español',
      // labels
      'label.language': 'Idioma',
      'label.sort': 'Ordenar por',
      'label.reverseOrder': 'Invertir orden',
      'label.verifyingEmail': 'Verificando email',
      'label.login': 'Acceder',
      'label.logout': 'Salir',
      'label.newAccount': 'Crear cuenta',
      'label.fixedPriceAlerts': 'Alertas por precio',
      'label.newCurrencyAlerts': 'Alertas de nuevas monedas',
      'label.display': 'Mostrar',
      'label.alertTypes': 'Tipos de alertas que puedes recibir',
      'label.supportedExchanges': 'Exchanges soportados',
      'label.emailNotReceived': '¿Ya te has registrado?, ¿Aún no has recibido el email para verificar tu cuenta?',
      'label.yourAlerts': 'Tus alertas',
      'label.createFixedPriceAlert': 'Crear alerta',
      // messages
      'message.serverUnavailable': 'El servidor no esta disponible, por favor, intenta de nuevo en un minuto',
      'message.welcome': 'Bienvenido',
      'message.bye': 'Tu sesión a finalizado',
      'message.authError': 'No tienes permisos, por favor, accede a tu cuenta',
      'message.verifyEmail': `Por favor, accede a tu email ({{email}}) para verificar tu cuenta. <hr>
Si {{email}} no es tu email, por favor, vuelve átras e ingresa el email correcto. <hr>
Si no has recibido nuestro email en 15 minutos, por favor verifica tu correo no deseado (spam). <hr>
¿Aún no lo encuentras? Intenta buscando esto en Gmail "in:all subject:(Confirm your account on Crypto Coin Alerts)" (sin comillas).`,

      'message.accountNeeded.question': '¿Qué esperas? Crea una',
      'message.accountNeeded.new': 'nueva cuenta',
      'message.accountNeeded.or': 'o',
      'message.accountNeeded.login': 'ingresa',
      'message.accountNeeded.last': 'para comenzar a registrar alertas',

      'message.emailVerified': 'Gracias por verificar tu cuenta',
      'message.resolveCaptcha': 'Resuelve el reCAPTCHA',
      'message.alertCreated': 'Alerta creada',
      'message.confirmDeleteAlert': 'La alerta será borrada, ¿continuar?',
      'message.alertDeleted': 'Alerta borrada',
      'message.yourAboveFixedPriceAlert': 'Te notificaremos cuando {{currency}} este arriba de {{price}} {{market}} en {{exchange}}',
      'message.yourBelowFixedPriceAlert': 'Te notificaremos cuando {{currency}} este abajo de {{price}} {{market}} en {{exchange}}',
      'message.newCurrencyAlertCreated': 'Alerta creada para {{exchange}}',
      'message.newCurrencyAlertDeleted': 'Alerta borrada para {{exchange}}',
      // field names
      'field.email': 'Email',
      'field.password': 'Constraseña',
      'field.repeatPassword': 'Repite contraseña',
      'field.exchange': 'Exchange',
      'field.market': 'Mercado',
      'field.currency': 'Moneda',
      'field.priceCondition': 'Condición del precio',
      'field.above': 'Arriba de',
      'field.below': 'Abajo de',
      'field.price': 'Precio',
      'field.basePrice': 'Precio cuando compraste / vendiste',
      'field.optional': 'opcional',
      'field.createdOn': 'Creado en',
      'field.triggeredOn': 'Enviado en',
      'field.pendingAlerts': 'Alertas pendientes',
      'field.triggeredAlerts': 'Alertas enviadas',
      'field.all': 'Todas',
      // actions
      'action.createAccount': 'Crear cuenta',
      'action.login': 'Entrar',
      'action.createAlert': 'Crear alerta',
      'action.newAlert': 'Nueva alerta',
      'action.cancel': 'Cancelar',
      'action.delete': 'Borrar',
      // custom validations
      'validation.passwordMismatch': 'las contraseñas no coinciden'
    };
  }
}
