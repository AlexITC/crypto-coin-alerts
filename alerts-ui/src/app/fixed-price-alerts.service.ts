import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { environment } from '../environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class FixedPriceAlertsService {

  private baseUrl = environment.api.url + '/fixed-price-alerts';

  constructor(private http: HttpClient) { }

  getUntriggeredAlerts(offset: number, limit: number): Observable<any> {
    const url = `${this.baseUrl}?offset=${offset}&limit=${limit}&filter=triggered:false`;
    return this.http.get<any>(url);
  }

  create(exchangeCurrencyId: number, price: number, isGreaterThan: boolean, basePrice: number) {
    const data = {
      exchangeCurrencyId: +exchangeCurrencyId,
      price: price,
      isGreaterThan: isGreaterThan
    };

    if (basePrice != null) {
      data['basePrice'] = +basePrice;
    }

    return this.http.post<any>(this.baseUrl, data, httpOptions);
  }

  delete(id: number): Observable<any> {
    const url = `${this.baseUrl}/${id}`;

    return this.http.delete(url);
  }
}
