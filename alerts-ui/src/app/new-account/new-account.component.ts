import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { isComponentView } from '@angular/core/src/view/util';
import { unescape } from 'querystring';

@Component({
  selector: 'app-new-account',
  templateUrl: './new-account.component.html',
  styleUrls: ['./new-account.component.css']
})
export class NewAccountComponent implements OnInit {

  form: FormGroup;
  submitted = false;

  constructor(private formBuilder: FormBuilder) {
    this.createForm();
  }

  matchingPasswords(passwordKey: string, repeatPasswordKey: string) {
    return (group: FormGroup): { [key: string]: any } => {
      const password = group.controls[passwordKey];
      const repeatPassword = group.controls[repeatPasswordKey];

      if (password.value !== repeatPassword.value) {
        const errors = { passwordMismatch: true };
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
    console.log('submitting form');
    // TODO: call the web service
  }

  hasWrongValue(fieldName: string): boolean {
    return this.getFieldErrors(fieldName).length > 0;
  }

  hasCorrectValue(fieldName: string): boolean {
    const control = this.findFieldControl(name);
    // field found && user changed it && it doesn't hold a wrong value
    const isCorrect = control && !control.pristine && !this.hasWrongValue(fieldName);

    return isCorrect;
  }

  // right now we are rendering one error only for a field
  getFieldError(fieldName: string): string {
    return this.getFieldErrors(fieldName)[0];
  }

  getFieldErrors(fieldName: string): string[] {
    const control = this.findFieldControl(fieldName);
    if (control && (control.touched || this.submitted) && control.errors) {
      return this.getErrors(control);
    } else {
      return [];
    }
  }

  protected getErrors(control: AbstractControl): string[] {
    return Object.keys(control.errors)
      .filter((error) => control.errors[error])
      .map((error) => {
        const params = control.errors[error];
        return error;
      });
  }

  protected findFieldControl(fieldName: string): AbstractControl {
    // TODO: verify why I'm not retrieving the control from the form
    if (this.form.contains(fieldName)) {
      return this.form.get(fieldName);
    } else {
      return this.form;
    }
  }
}
