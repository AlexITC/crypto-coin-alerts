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
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private usersService: UsersService,
    private authService: AuthService,
    public errorService: ErrorService) {

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
    // TODO: Disable submit button to avoid sending the same request twice
    this.usersService
      .login(this.form.get('email').value, this.form.get('password').value)
      .subscribe(
        response => this.onSubmitSuccess(response),
        response => this.errorService.renderServerErrors(this.form, response)
      );
  }

  protected onSubmitSuccess(response: AuthorizationToken) {
    // TODO: do something useful
    console.log('Logged in: ' + JSON.stringify(response));
    this.authService.setToken(response);
  }
}
