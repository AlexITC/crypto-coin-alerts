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
    { label: 'Fixed price alerts', path: 'fixed-price-alerts' }
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
    this.authService.setToken(null);
  }
}
