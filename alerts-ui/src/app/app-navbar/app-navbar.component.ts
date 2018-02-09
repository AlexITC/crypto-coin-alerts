import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { TranslateService } from '@ngx-translate/core';

import { AuthService } from '../auth.service';
import { NavigatorService } from '../navigator.service';
import { NotificationService } from '../notification.service';
import { LanguageService } from '../language.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './app-navbar.component.html',
  styleUrls: ['./app-navbar.component.css']
})
export class AppNavbarComponent implements OnInit {

  public tabs = [
    { label: 'label.fixedPriceAlerts', path: '/fixed-price-alerts', authRequired: true },
    { label: 'label.newCurrencyAlerts', path: '/new-currency-alerts', authRequired: true }
  ];

  constructor(
    private authService: AuthService,
    private navigatorService: NavigatorService,
    private notificationService: NotificationService,
    private languageService: LanguageService,
    private translate: TranslateService,
    private location: Location) { }

  ngOnInit() {
  }

  /* tabs */
  isSelected(path: string): boolean {
    if (!path.startsWith('/')) {
      path = '/' + path;
    }

    return this.location.isCurrentPathEqualTo(path);
  }

  isDisplayable(tab: any): boolean {
    if (tab.authRequired === true) {
      return this.isAuthenticated();
    } else {
      return true;
    }
  }

  /* user */
  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  getAuthenticatedUser() {
    if (this.isAuthenticated()) {
      return this.authService.getAuthenticatedUser().email;
    } else {
      return undefined;
    }
  }

  logout() {
    const email = this.getAuthenticatedUser();
    this.translate.get('message.bye')
      .subscribe(msg => this.notificationService.info(`${msg} ${email}`));

    // TODO: invalidate token on the server
    this.authService.setToken(null);
    this.navigatorService.home();
  }

  /* lang */
  setLang(lang: string) {
    this.languageService.setLang(lang);
  }
}
