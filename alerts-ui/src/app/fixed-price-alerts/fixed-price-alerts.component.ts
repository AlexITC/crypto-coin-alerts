import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import 'rxjs/add/operator/do';
import 'rxjs/add/operator/map';

import { TranslateService } from '@ngx-translate/core';

import { FixedPriceAlertsService } from '../fixed-price-alerts.service';
import { ExchangeCurrencyService } from '../exchange-currency.service';
import { ExchangeCurrency } from '../exchange-currency';
import { NotificationService } from '../notification.service';
import { ErrorService } from '../error.service';

@Component({
  selector: 'app-fixed-price-alerts',
  templateUrl: './fixed-price-alerts.component.html',
  styleUrls: ['./fixed-price-alerts.component.css']
})
export class FixedPriceAlertsComponent implements OnInit {

  orderBy = 'createdOn';
  reverseOrder = true;
  triggered = 'false'; // non-triggered alerts by default

  // pagination
  total = 0;
  currentPage = 1;
  pageSize = 10;
  asyncItems: Observable<any>;

  constructor(
    private translate: TranslateService,
    private notificationService: NotificationService,
    private errorService: ErrorService,
    private fixedPriceAlertsService: FixedPriceAlertsService,
    private exchangeCurrencyService: ExchangeCurrencyService) { }

  ngOnInit() {
    this.getPage(this.currentPage);
  }

  getPage(page: number) {
    const offset = (page - 1) * this.pageSize;
    const limit = this.pageSize;
    const orderBy = this.orderBy + ':' + (this.reverseOrder ? 'desc' : 'asc');
    const filter = `triggered:${this.triggered}`;

    this.asyncItems = this.fixedPriceAlertsService
      .getAlerts(offset, limit, filter, orderBy)
      .do(response => this.total = response.total)
      .do(response => this.currentPage = 1 + (response.offset / this.pageSize))
      .map(response => response.data);
  }

  delete(alert: any) {
    this.translate.get('message.confirmDeleteAlert')
      .subscribe(msg => {
        // TODO: use our own modal
        if (confirm(msg)) {
          this.fixedPriceAlertsService.delete(alert.id).subscribe(
            response => this.onAlertDeleted(),
            response => this.onAlertNotDeleted(response)
          );
        }
      });
  }

  private onAlertDeleted() {
    this.translate.get('message.alertDeleted')
      .subscribe(msg => this.notificationService.info(msg));

    this.reloadPage();
  }

  private onAlertNotDeleted(response: any) {
    this.errorService.renderServerErrors(null, response);

    // one reason could be that the alert has been triggered, reloading data could help
    this.reloadPage();
  }

  reloadPage() {
    this.getPage(this.currentPage);
  }
}
