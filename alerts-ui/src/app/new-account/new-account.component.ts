import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { ReCaptchaComponent } from 'angular2-recaptcha';

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

  @ViewChild(ReCaptchaComponent) captcha: ReCaptchaComponent;
  form: FormGroup;
  createdEmail: string;

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
        Validators.maxLength(64)
      ])],
      password: ['', Validators.compose([
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(30)
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
    this.createdEmail = response.email;
  }

  protected onSubmitError(response) {
    this.errorService.renderServerErrors(this.form, response);
    this.captcha.reset();
    this.reCaptchaResponse = null;
  }
}
