import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UsersService } from '../users.service';

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
    private usersService: UsersService) {

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
        response => this.onSubmitError(response)
      );
  }

  protected onSubmitSuccess(response: any) {
    // TODO: do something useful
    console.log('Logged in: ' + JSON.stringify(response));
  }

  protected onSubmitError(response: any): any {
    console.log('error: ' + JSON.stringify(response));
    response.error.errors.forEach((element: any) => {
      // field errors are handled here, different errors should be handled globally
      if (element.type === 'field-validation-error') {
        const fieldName = element.field;
        const message = element.message;
        this.setFieldError(fieldName, message);
      }
    });
  }

  // TODO: most logic here is repeated
  protected setFieldError(fieldName: string, message: string) {
    const control = this.findFieldControl(fieldName);
    // TODO: find a better way to set the error message
    const errors = { [message]: true };
    control.setErrors(errors);
  }

  hasWrongValue(fieldName: string): boolean {
    return this.getFieldErrors(fieldName).length > 0;
  }

  hasCorrectValue(fieldName: string): boolean {
    const control = this.findFieldControl(fieldName);
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
      .filter((error: any) => control.errors[error])
      .map((error: any) => {
        const params = control.errors[error];
        return error;
      });
  }

  protected findFieldControl(fieldName: string): AbstractControl {
    return this.form.get(fieldName);
  }
}
