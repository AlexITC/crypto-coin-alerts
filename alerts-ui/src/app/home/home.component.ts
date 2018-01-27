import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  alertTypesCollapsed = true;
  supportedExchangesCollapsed = true;
  emailNotReceivedHelpCollapsed = true;

  constructor(public authService: AuthService) { }

  ngOnInit() {
  }

}
