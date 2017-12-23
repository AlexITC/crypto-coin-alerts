import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { environment } from '../environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

class User {
  id: string;
  email: string;
}

class AuthorizationToken {
  token: string;
}

@Injectable()
export class UsersService {

  private baseUrl = environment.api.url + '/users';

  constructor(private http: HttpClient) { }

  create(email: string, password: string): Observable<User> {
    const body = { email: email, password: password };
    return this.http.post<User>(this.baseUrl, body, httpOptions);
  }

  login(email: string, password: string): Observable<AuthorizationToken> {
    const body = { email: email, password: password };
    const url = this.baseUrl + '/login';
    return this.http.post<AuthorizationToken>(url, body, httpOptions);
  }
}
