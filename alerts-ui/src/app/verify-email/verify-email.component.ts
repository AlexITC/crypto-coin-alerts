import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { UsersService } from '../users.service';
import { ErrorService } from '../error.service';
import { AuthService } from '../auth.service';
import { AuthorizationToken } from '../authorization-token';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css']
})
export class VerifyEmailComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private errorService: ErrorService,
    private usersService: UsersService,
    private authService: AuthService) { }

  ngOnInit() {
    const token = this.route.snapshot.paramMap.get('token');
    this.usersService.verifyEmail(token).subscribe(
      response => this.onEmailVerified(response),
      response => this.onEmailVerifyError(response)
    );
  }

  onEmailVerified(response: AuthorizationToken) {
    this.authService.setToken(response);
    // TODO: add success message
    this.router.navigate(['/']);
  }

  onEmailVerifyError(response: any) {
    this.errorService.renderServerErrors(null, response);
    this.router.navigate(['/']);
  }
}
