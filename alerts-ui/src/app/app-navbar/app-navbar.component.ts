import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './app-navbar.component.html',
  styleUrls: ['./app-navbar.component.css']
})
export class AppNavbarComponent implements OnInit {

  constructor(private authService: AuthService) { }

  ngOnInit() {
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  getAuthenticatedUser() {
    if (this.isAuthenticated()) {
      // TODO: load user
      return this.authService.getToken().token.substr(0, 3);
    } else {
      return undefined;
    }
  }

  logout() {
    this.authService.setToken(null);
  }
}
