import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import 'rxjs/add/observable/of';

import { environment } from '../environments/environment';
import { ExchangeCurrency } from './exchange-currency';

@Injectable()
export class ExchangeCurrencyService {

  private baseUrl = environment.api.url + '/exchanges';

  // these data doesn't change frequently
  private exchangeMarketsCache = {};
  private marketCurrenciesCache = {};

  constructor(private http: HttpClient) { }

  /* caching */
  private hasExchangeMarkets(exchange: string): boolean {
    return this.exchangeMarketsCache[exchange] != null;
  }

  private cacheExchangeMarkets(exchange, markets: string[]) {
    this.exchangeMarketsCache[exchange] = markets;
  }

  private hasMarketCurrencies(exchange: string, market: string): boolean {
    return this.marketCurrenciesCache[exchange] != null &&
           this.marketCurrenciesCache[exchange][market] != null;
  }

  private cacheMarketCurrencies(exchange: string, market: string, currencies: ExchangeCurrency[]) {
    if (this.marketCurrenciesCache[exchange] == null) {
      this.marketCurrenciesCache[exchange] = {};
    }

    this.marketCurrenciesCache[exchange][market] = currencies;
  }

  /* actual methods */
  getMarkets(exchange: string): Observable<string[]> {
    if (exchange == null || exchange === '') {
      return Observable.of([]);
    }

    if (this.hasExchangeMarkets(exchange)) {
      return Observable.of(this.exchangeMarketsCache[exchange]);
    }

    const url = `${this.baseUrl}/${exchange}/markets`;
    return this.http.get<string[]>(url)
             .do(markets => this.cacheExchangeMarkets(exchange, markets));
  }

  getCurrencies(exchange: string, market: string): Observable<ExchangeCurrency[]> {
    if (market == null || market === '') {
      return Observable.of([]);
    }

    if (this.hasMarketCurrencies(exchange, market)) {
      return Observable.of(this.marketCurrenciesCache[exchange][market]);
    }

    const url = `${this.baseUrl}/${exchange}/markets/${market}/currencies`;
    return this.http.get<ExchangeCurrency[]>(url)
             .do(currencies => this.cacheMarketCurrencies(exchange, market, currencies));
  }
}
