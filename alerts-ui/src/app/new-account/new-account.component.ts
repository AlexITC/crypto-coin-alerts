import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { TranslateService } from '@ngx-translate/core';

import { UsersService } from '../users.service';
import { ErrorService } from '../error.service';
import { ReCaptchaService } from '../re-captcha.service';
import { NotificationService } from '../notification.service';
import { NavigatorService } from '../navigator.service';

@Component({
  selector: 'app-new-account',
  templateUrl: './new-account.component.html',
  styleUrls: ['./new-account.component.css']
})
export class NewAccountComponent implements OnInit {

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
      private navigatorService: NavigatorService,
      private usersService: UsersService,
      private notificationService: NotificationService,
      private translate: TranslateService,
      public errorService: ErrorService,
      public reCaptchaService: ReCaptchaService) {

    this.createForm();
    // required to get the reCAPTCHA response
    window['onCaptchaResolved'] = this.onCaptchaResolved.bind(this);
    window['onCaptchaExpired'] = this.onCaptchaExpired.bind(this);
  }

  matchingPasswords(passwordKey: string, repeatPasswordKey: string) {
    return (group: FormGroup): { [key: string]: any } => {
      const password = group.controls[passwordKey];
      const repeatPassword = group.controls[repeatPasswordKey];

      if (password.value !== repeatPassword.value) {
        const errors = { 'validation.passwordMismatch': true };
        group.get(repeatPasswordKey).setErrors(errors);
      }

      return undefined;
    };
  }

  createForm() {
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
      ])],
      repeatPassword: ['', Validators.required]
    }, { validator: this.matchingPasswords('password', 'repeatPassword') });
  }

  ngOnInit() {}

  onSubmit() {
    if (this.reCaptchaResponse == null) {
      this.translate.get('message.resolveCaptcha')
        .subscribe(msg => this.notificationService.error(msg));

      return;
    }

    this.usersService
      .create(
        this.form.get('email').value,
        this.form.get('password').value,
        this.reCaptchaResponse)
      .subscribe(
        response => this.onSubmitSuccess(response),
        response => this.onSubmitError(response)
      );
  }

  protected onSubmitSuccess(response) {
    this.translate.get('message.verifyEmail')
      .subscribe(msg => this.notificationService.info(msg));

    this.navigatorService.home();
  }

  protected onSubmitError(response) {
    this.errorService.renderServerErrors(this.form, response);
    (<any>window).grecaptcha.reset();
  }
}
