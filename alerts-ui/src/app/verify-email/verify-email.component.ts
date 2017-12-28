import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';

import { UsersService } from '../users.service';
import { ErrorService } from '../error.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css']
})
export class VerifyEmailComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private errorService: ErrorService,
    private usersService: UsersService,
    private location: Location) { }

  ngOnInit() {
    const token = this.route.snapshot.paramMap.get('token');
    console.log('Verifying email for: ' + token);
    this.usersService.verifyEmail(token).subscribe(
      response => this.onEmailVerified(response),
      response => this.onEmailVerifyError(response)
    );
  }

  onEmailVerified(response: any) {
    console.log('Email verified: ' + response);
    // TODO: store token and ensure redirect is working
    this.location.go('/login');
  }

  onEmailVerifyError(response: any) {
    this.errorService.renderServerErrors(null, response)
    this.location.go('/');
  }
}
