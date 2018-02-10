import { Injectable } from '@angular/core';

import { TranslateService } from '@ngx-translate/core';

export const DEFAULT_LANG = 'en';
export const SUPPORTED_LANGS = ['en', 'es'];

@Injectable()
export class LanguageService {

  // keep lang in-memory to be able to react to changes
  lang: string;

  constructor(private translate: TranslateService) { }

  getLang(): string {
    if (this.lang == null) {
      this.lang = localStorage.getItem('lang');
    }

    return this.lang || this.defaultLang();
  }

  setLang(lang: string) {
    this.lang = lang;

    localStorage.setItem('lang', lang);
    this.translate.use(lang);
  }

  // quite simplle function to detect preferred lang based on the browser lang
  private defaultLang(): string {
    const browserLang = (window.navigator.language || DEFAULT_LANG);
    const preferredLang = browserLang.substr(0, 2);

    if (SUPPORTED_LANGS.indexOf(preferredLang) >= 0) {
      return preferredLang;
    } else {
      return DEFAULT_LANG;
    }
  }
}
