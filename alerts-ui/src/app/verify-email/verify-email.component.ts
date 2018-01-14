import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';

import { UsersService } from '../users.service';
import { ErrorService } from '../error.service';
import { AuthService } from '../auth.service';
import { AuthorizationToken } from '../authorization-token';
import { NotificationService } from '../notification.service';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css']
})
export class VerifyEmailComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService,
    private translate: TranslateService,
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
    this.translate.get('message.emailVerified')
      .subscribe(msg => this.notificationService.info(msg));

    this.translate.get('message.welcome')
      .subscribe(msg => this.notificationService.info(`${msg} ${this.authService.getAuthenticatedUser().email}`));

    this.router.navigate(['/']);
  }

  onEmailVerifyError(response: any) {
    this.errorService.renderServerErrors(null, response);
    this.router.navigate(['/']);
  }
}
