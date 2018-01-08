import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { AuthService } from '../auth.service';
import { UsersService } from '../users.service';
import { AuthorizationToken } from '../authorization-token';
import { ErrorService } from '../error.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

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
    public errorService: ErrorService) {

    this.createForm();

    // required to get the reCAPTCHA response
    window['onCaptchaResolved'] = this.onCaptchaResolved.bind(this);
    window['onCaptchaExpired'] = this.onCaptchaExpired.bind(this);
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
      // TODO: i18n
      this.errorService.renderError('Resolve the CAPTCHA');
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
    // TODO: do something useful
    console.log('Logged in: ' + JSON.stringify(response));
    this.authService.setToken(response);
  }

  protected onSubmitError(response) {
    this.errorService.renderServerErrors(this.form, response);
    (<any>window).grecaptcha.reset();
  }
}
