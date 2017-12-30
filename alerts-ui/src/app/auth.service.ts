import { Injectable } from '@angular/core';

import { AuthorizationToken } from './authorization-token';
import { User } from './user';

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

  getAuthenticatedUser(): User {
    const token = this.getToken();
    if (token != null) {
      const claims = this.getClaims(token.token);
      return {
        id: claims.id,
        email: claims.email
      };
    } else {
      return undefined;
    }
  }

  // function based on https://github.com/ttkalec/laravel5-angular-jwt/blob/master/public/scripts/services.js#L6
  private urlBase64Decode(str: string) {
    let output = str.replace('-', '+');
    switch (output.length % 4) {
      case 0: break;
      case 2: output += '=='; break;
      case 3: output += '='; break;
      default: throw new Error('Invalid token');
    }
    return window.atob(output);
  }

  // function based on https://github.com/ttkalec/laravel5-angular-jwt/blob/master/public/scripts/services.js#L23
  private getClaims(token: string) {
    const encoded = token.split('.')[1];
    const decoded = this.urlBase64Decode(encoded);
    const claims = JSON.parse(decoded);
    return claims;
  }
}
