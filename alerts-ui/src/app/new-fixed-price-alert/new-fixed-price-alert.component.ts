import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs/Observable';

import { TranslateService } from '@ngx-translate/core';

import { ErrorService } from '../error.service';
import { ExchangeCurrencyService } from '../exchange-currency.service';
import { ExchangeCurrency } from '../exchange-currency';
import { FixedPriceAlertsService } from '../fixed-price-alerts.service';
import { NavigatorService } from '../navigator.service';
import { NotificationService } from '../notification.service';

import { exchanges } from '../constants';

@Component({
  selector: 'app-new-fixed-price-alert',
  templateUrl: './new-fixed-price-alert.component.html',
  styleUrls: ['./new-fixed-price-alert.component.css']
})
export class NewFixedPriceAlertComponent implements OnInit {

  form: FormGroup;

  // TODO: find a way to not require this field
  selectedCurrency: ExchangeCurrency;

  // TODO: load them from the server?
  availableExchanges = exchanges;
  availableMarkets: Observable<string[]>;
  availableCurrencies: Observable<ExchangeCurrency[]>;

  constructor(
    private formBuilder: FormBuilder,
    private translate: TranslateService,
    private navigatorService: NavigatorService,
    private notificationService: NotificationService,
    private exchangeCurrencyService: ExchangeCurrencyService,
    private fixedPriceAlertsService: FixedPriceAlertsService,
    public errorService: ErrorService) {

    this.createForm();
  }

  ngOnInit() {
  }

  private createForm() {
    const priceValidators = [Validators.min(0.00000001), Validators.max(99999999)];

    this.form = this.formBuilder.group({
      exchange: [null, Validators.required],
      market: [null, Validators.required],
      currency: [null, Validators.required],
      condition: [null, Validators.required],
      price: [null, [
        Validators.required,
        ...priceValidators
      ]],
      basePrice: [null, priceValidators]
    });

    this.disableCurrency();
    this.disableMarket();
  }

  private disableMarket() {
    this.form.get('market').disable();
  }

  private enableMarket() {
    this.form.get('market').enable();
  }

  private disableCurrency() {
    this.form.get('currency').disable();
  }

  private enableCurrency() {
    this.form.get('currency').enable();
  }

  // TODO: reusable function
  private sortStrings(array: string[]): string[] {
    return array.sort((a, b) => {
      if (a < b) {
        return -1;
      } else if (a > b) {
        return 1;
      } else {
        return 0;
      }
    });
  }

  // TODO: reusable function
  private sortCurrencies(array: ExchangeCurrency[]): ExchangeCurrency[] {
    return array.sort((a, b) => {
      if (a.currency < b.currency) {
        return -1;
      } else if (a.currency > b.currency) {
        return 1;
      } else {
        return 0;
      }
    });
  }

  onExchangeSelected(exchange: string) {
    this.availableCurrencies = null;
    this.availableMarkets = this.exchangeCurrencyService
      .getMarkets(exchange)
      .map(markets => this.sortStrings(markets));

    this.disableCurrency();
    this.enableMarket();
  }

  onMarketSelected(market: string) {
    const exchange = this.form.get('exchange').value;
    this.availableCurrencies = this.exchangeCurrencyService
      .getCurrencies(exchange, market)
      .map(currencies => this.sortCurrencies(currencies));

    this.enableCurrency();
  }

  onSubmit() {
    this.fixedPriceAlertsService.create(
        this.selectedCurrency.id,
        this.form.get('price').value,
        this.form.get('condition').value === 'above',
        this.form.get('basePrice').value)
      .subscribe(
        response => this.onSubmitSuccess(response),
        response => this.errorService.renderServerErrors(this.form, response)
      );
  }

  protected onSubmitSuccess(response: any) {
    this.translate.get('message.alertCreated')
      .subscribe(msg => this.notificationService.info(msg));

    this.navigatorService.fixedPriceAlerts();
  }

  /* Help block with the summary of the alert */
  displayMessage(): boolean {
    return this.form.get('condition').value != null &&
      this.form.get('exchange').value != null &&
      this.form.get('market').value != null &&
      this.selectedCurrency != null;
  }

  messageKey() {
    if (this.form.get('condition').value === 'above') {
      return 'message.yourAboveFixedPriceAlert';
    } else {
      return 'message.yourBelowFixedPriceAlert';
    }
  }

  messageParams() {
    const params = {
      exchange: this.form.get('exchange').value,
      market: this.form.get('market').value,
      currency: this.selectedCurrency.currency,
      price: this.form.get('price').value
    };

    return params;
  }
}
