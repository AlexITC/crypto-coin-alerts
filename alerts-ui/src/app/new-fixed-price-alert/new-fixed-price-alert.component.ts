import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { ErrorService } from '../error.service';

@Component({
  selector: 'app-new-fixed-price-alert',
  templateUrl: './new-fixed-price-alert.component.html',
  styleUrls: ['./new-fixed-price-alert.component.css']
})
export class NewFixedPriceAlertComponent implements OnInit {

  form: FormGroup;

  // TODO: load it from the server
  availableExchanges = ['BITSO', 'BITTREX'];

  // TODO: load it from the server
  private exchangeMarkets = {
    'BITSO': ['BTC', 'ETH', 'XRP', 'BCH', 'LTC'],
    'BITTREX': ['BTC', 'ETH', 'USDT']
  };

  availableMarkets(exchange: string) {
    return this.exchangeMarkets[exchange] || [];
  }

  constructor(
    private formBuilder: FormBuilder,
    public errorService: ErrorService) {

    this.createForm();
  }

  ngOnInit() {
  }

  private createForm() {
    const priceValidators = [Validators.min(0.00000001), Validators.max(99999999)];

    this.form = this.formBuilder.group({
      exchange: ['', [
        Validators.required
      ]],
      market: ['', [Validators.required]],
      currency: ['', [
        Validators.required // TODO: restrict to known books
      ]],
      isGreaterThan: [''],
      price: ['', [
        Validators.required,
        ...priceValidators
      ]],
      basePrice: ['', priceValidators]
    });
  }

  onSubmit() {
    console.log('Submitting: ' + JSON.stringify(this.form.getRawValue()));
  }

  protected onSubmitSuccess(response: any) {
    // TODO: do something useful
    console.log('onSubmitSuccess: ' + JSON.stringify(response));
  }
}
