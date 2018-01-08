import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import 'rxjs/add/operator/do';
import 'rxjs/add/operator/map';

import { FixedPriceAlertsService } from '../fixed-price-alerts.service';

@Component({
  selector: 'app-fixed-price-alerts',
  templateUrl: './fixed-price-alerts.component.html',
  styleUrls: ['./fixed-price-alerts.component.css']
})
export class FixedPriceAlertsComponent implements OnInit {

  total = 0;
  currentPage = 1;
  pageSize = 10;
  asyncItems: Observable<any>;

  constructor(private fixedPriceAlertsService: FixedPriceAlertsService) { }

  ngOnInit() {
    this.getPage(this.currentPage);
  }

  getPage(page: number) {
    const offset = (page - 1) * this.pageSize;
    const limit = this.pageSize;

    this.asyncItems = this.fixedPriceAlertsService
      .get(offset, limit)
      .do(response => this.total = response.total)
      .map(response => response.data);
  }

}
