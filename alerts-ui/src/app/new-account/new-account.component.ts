import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UsersService } from '../users.service';
import { ErrorService } from '../error.service';

@Component({
  selector: 'app-new-account',
  templateUrl: './new-account.component.html',
  styleUrls: ['./new-account.component.css']
})
export class NewAccountComponent implements OnInit {

  form: FormGroup;

  constructor(
      private formBuilder: FormBuilder,
      private usersService: UsersService,
      public errorService: ErrorService) {

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
        Validators.maxLength(50)
      ])],
      password: ['', Validators.compose([
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(50)
      ])],
      repeatPassword: ['', Validators.compose([
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(50)
      ])],
    }, { validator: this.matchingPasswords('password', 'repeatPassword') });
  }

  ngOnInit() {}

  onSubmit() {
    // TODO: Disable submit button to avoid sending the same request twice
    this.usersService
      .create(this.form.get('email').value, this.form.get('password').value)
      .subscribe(
        response => this.onSubmitSuccess(response),
        response => this.errorService.renderServerErrors(this.form, response)
      );
  }

  protected onSubmitSuccess(response) {
    // TODO: do something useful
    console.log('user created: ' + JSON.stringify(response));
  }
}
