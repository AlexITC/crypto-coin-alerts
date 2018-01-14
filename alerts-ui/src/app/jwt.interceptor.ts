import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/do';

import { TranslateService } from '@ngx-translate/core';

import { AuthService } from './auth.service';
import { NavigatorService } from './navigator.service';
import { NotificationService } from './notification.service';

@Injectable()
export class JWTInterceptor implements HttpInterceptor {

  constructor(
    private translate: TranslateService,
    private notificationService: NotificationService,
    private navigatorService: NavigatorService,
    public authService: AuthService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    if (this.authService.isAuthenticated()) {
      const newHeaders = {
        Authorization: `Bearer ${this.authService.getToken().token}`
      };

      request = request.clone({
        setHeaders: newHeaders
      });
    }

    return next.handle(request).do((event: HttpEvent<any>) => {
      if (event instanceof HttpResponse) {
        if (event.status === 401 || event.status === 403) {
          this.onAuthFailure();
        }
      }

      return event;
    }, (error: any) => {
      if (error instanceof HttpErrorResponse) {
        if (error.status === 401 || error.status === 403) {
          this.onAuthFailure();
        }
      }
    });
  }

  private onAuthFailure() {
    this.authService.setToken(null);
    this.translate.get('message.authError')
      .subscribe(msg => this.notificationService.error(msg));

    this.navigatorService.login();
  }
}
