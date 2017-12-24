import { Injectable } from '@angular/core';

import { AuthorizationToken } from './authorization-token';

@Injectable()
export class AuthService {

  // keep token in-memory to be able to be able to react to changes
  token: AuthorizationToken;

  constructor() { }

  isAuthenticated(): boolean {
    const token = this.getToken();

    // TODO: check token validity
    return token != null && token.token != null && token.token.length > 0;
  }

  getToken(): AuthorizationToken {
    if (this.token == null) {
      const jwt = localStorage.getItem('jwt') || '{}';
      this.token = JSON.parse(jwt);
    }

    return this.token;
  }

  setToken(token: AuthorizationToken) {
    this.token = token;
    localStorage.setItem('jwt', JSON.stringify(token));
  }
}
