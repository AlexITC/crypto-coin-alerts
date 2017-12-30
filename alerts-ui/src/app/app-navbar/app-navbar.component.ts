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
      return this.authService.getAuthenticatedUser().email;
    } else {
      return undefined;
    }
  }

  logout() {
    this.authService.setToken(null);
  }
}
