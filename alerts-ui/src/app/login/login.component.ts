import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { ReCaptchaComponent } from 'angular2-recaptcha';

import { TranslateService } from '@ngx-translate/core';

import { AuthService } from '../auth.service';
import { UsersService } from '../users.service';
import { AuthorizationToken } from '../authorization-token';
import { ErrorService } from '../error.service';
import { ReCaptchaService } from '../re-captcha.service';
import { NotificationService } from '../notification.service';
import { NavigatorService } from '../navigator.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  @ViewChild(ReCaptchaComponent) captcha: ReCaptchaComponent;
  form: FormGroup;

  private reCaptchaResponse: string;

  onCaptchaResolved(response: string) {
    this.reCaptchaResponse = response;
  }

  onCaptchaExpired() {
    this.reCaptchaResponse = null;
  }

  constructor(
    private formBuilder: FormBuilder,
    private usersService: UsersService,
    private authService: AuthService,
    private notificationService: NotificationService,
    private navigatorService: NavigatorService,
    private translate: TranslateService,
    public errorService: ErrorService,
    public reCaptchaService: ReCaptchaService) {

    this.createForm();
  }

  ngOnInit() { }

  private createForm() {
    this.form = this.formBuilder.group({
      email: ['', Validators.compose([
        Validators.required,
        Validators.email,
        Validators.minLength(6),
        Validators.maxLength(50)
      ])],
      password: ['', Validators.compose([
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(50)
      ])]
    });
  }

  onSubmit() {
    if (this.reCaptchaResponse == null) {
      this.translate.get('message.resolveCaptcha')
        .subscribe(msg => this.notificationService.error(msg));

      return;
    }

    this.usersService
      .login(
        this.form.get('email').value,
        this.form.get('password').value,
        this.reCaptchaResponse)
      .subscribe(
        response => this.onSubmitSuccess(response),
        response => this.onSubmitError(response)
      );
  }

  protected onSubmitSuccess(response: AuthorizationToken) {
    this.authService.setToken(response);
    this.translate.get('message.welcome')
      .subscribe(msg => this.notificationService.info(`${msg} ${this.authService.getAuthenticatedUser().email}`));

    this.navigatorService.home();
  }

  protected onSubmitError(response) {
    this.errorService.renderServerErrors(this.form, response);
    this.captcha.reset();
    this.reCaptchaResponse = null;
  }
}
