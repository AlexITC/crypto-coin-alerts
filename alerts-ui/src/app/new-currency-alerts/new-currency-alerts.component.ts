import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { TranslateService } from '@ngx-translate/core';

import { NewCurrencyAlertsService } from '../new-currency-alerts.service';
import { NotificationService } from '../notification.service';
import { ErrorService } from '../error.service';

@Component({
  selector: 'app-new-currency-alerts',
  templateUrl: './new-currency-alerts.component.html',
  styleUrls: ['./new-currency-alerts.component.css']
})
export class NewCurrencyAlertsComponent implements OnInit {

  exchanges = ['BITSO', 'BITTREX'];
  values: any = {};

  constructor(
    private translate: TranslateService,
    private newCurrencyAlertsService: NewCurrencyAlertsService,
    private errorService: ErrorService,
    private notificationService: NotificationService) { }

  ngOnInit() {
    this.resetValues();
    this.reload();
  }

  reload() {
    this.newCurrencyAlertsService.getAlerts()
      .subscribe(
        response => this.onAlertsRetrieved(response),
        response => this.onServerError(response)
      );
  }

  resetValues() {
    this.values = {
      BITSO: false,
      BITTREX: false
    };
  }

  switchState(exchange: string, value: boolean) {
    if (value) {
      this.createAlert(exchange);
    } else {
      this.deleteAlert(exchange);
    }
  }

  private createAlert(exchange) {
    this.newCurrencyAlertsService
      .create(exchange)
      .subscribe(
        response => this.onAlertCreated(response),
        response => this.onServerError(response));
  }

  private deleteAlert(exchange) {
    this.newCurrencyAlertsService
      .delete(exchange)
      .subscribe(
        response => this.onAlertDeleted(response),
        response => this.onServerError(response));
  }

  private onAlertCreated(response: any) {
    this.translate.get('message.newCurrencyAlertCreated', response)
      .subscribe(msg => this.notificationService.info(msg));

    this.reload();
  }

  private onAlertDeleted(response: any) {
    this.translate.get('message.newCurrencyAlertDeleted', response)
      .subscribe(msg => this.notificationService.info(msg));

    this.reload();
  }

  private onAlertsRetrieved(alerts: any[]) {
    this.resetValues();
    alerts.forEach(alert => this.values[alert.exchange] = true);
  }

  private onServerError(response: any) {
    this.errorService.renderServerErrors(null, response);
  }
}
