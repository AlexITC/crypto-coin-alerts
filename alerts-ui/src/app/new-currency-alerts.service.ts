import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { environment } from '../environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class NewCurrencyAlertsService {

  private baseUrl = environment.api.url + '/new-currency-alerts';

  constructor(private http: HttpClient) { }

  getAlerts(): Observable<any> {
    return this.http.get(this.baseUrl);
  }

  create(exchange: string): Observable<any> {
    const url = `${this.baseUrl}/${exchange}`;

    return this.http.post<any>(url, '{}', httpOptions);
  }

  delete(exchange: string): Observable<any> {
    const url = `${this.baseUrl}/${exchange}`;

    return this.http.delete(url);
  }
}
