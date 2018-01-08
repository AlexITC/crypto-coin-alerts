import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { environment } from '../environments/environment';
import { ExchangeCurrency } from './exchange-currency';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class ExchangeCurrencyService {

  private baseUrl = environment.api.url + '/exchanges';

  constructor(private http: HttpClient) { }

  getMarkets(exchange: string): Observable<string[]> {
    const url = `${this.baseUrl}/${exchange}/markets`;

    return this.http.get<string[]>(url, httpOptions);
  }

  getCurrencies(exchange: string, market: string): Observable<ExchangeCurrency[]> {
    const url = `${this.baseUrl}/${exchange}/markets/${market}`;

    return this.http.get<ExchangeCurrency[]>(url, httpOptions);
  }
}
