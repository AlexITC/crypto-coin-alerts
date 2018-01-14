import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { AuthService } from '../auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './app-navbar.component.html',
  styleUrls: ['./app-navbar.component.css']
})
export class AppNavbarComponent implements OnInit {

  public tabs = [
    { label: 'label.fixedPriceAlerts', path: '/fixed-price-alerts', authRequired: true }
  ];

  constructor(
    private authService: AuthService,
    private location: Location) { }

  ngOnInit() {
  }

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
    // TODO: invalidate token on the server
    this.authService.setToken(null);
  }
}
