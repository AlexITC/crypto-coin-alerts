import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { LanguageService } from './language.service';

@Injectable()
export class LangInterceptor implements HttpInterceptor {

  constructor(
    public languageService: LanguageService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const newHeaders = {
      'Accept-Language': this.languageService.getLang()
    };

    const newRequest = request.clone({
      setHeaders: newHeaders
    });

    return next.handle(newRequest);
  }
}
