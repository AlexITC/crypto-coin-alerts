import { Injectable } from '@angular/core';

import { TranslateService } from '@ngx-translate/core';

export const DEFAULT_LANG = 'en';
export const SUPPORTED_LANGS = ['en'];

@Injectable()
export class LanguageService {

  // keep lang in-memory to be able to react to changes
  lang: string;

  constructor(private translate: TranslateService) { }

  getLang(): string {
    if (this.lang == null) {
      this.lang = localStorage.getItem('lang');
    }

    return this.lang || DEFAULT_LANG;
  }

  setLang(lang: string) {
    this.lang = lang;

    localStorage.setItem('lang', lang);
    this.translate.use(lang);
  }
}
