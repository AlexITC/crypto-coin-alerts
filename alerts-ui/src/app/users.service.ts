import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

class User {
  id: string;
  email: string;
}

@Injectable()
export class UsersService {

  // TODO: inject base url
  private baseUrl = 'http://localhost:9000/users';

  constructor(private http: HttpClient) { }

  create(email: string, password: string): Observable<User> {
    const body = { email: email, password: password };
    return this.http.post<User>(this.baseUrl, body, httpOptions);
  }
}
