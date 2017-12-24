import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { AuthService } from './auth.service';

@Injectable()
export class JWTInterceptor implements HttpInterceptor {

  constructor(public authService: AuthService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    if (this.authService.isAuthenticated()) {
      const newHeaders = {
        Authorization: `Bearer ${this.authService.getToken().token}`
      };

      request = request.clone({
        setHeaders: newHeaders
      });
    }

    return next.handle(request);
  }
}
