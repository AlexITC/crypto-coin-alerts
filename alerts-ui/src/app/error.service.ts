import { Injectable } from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms/src/model';

@Injectable()
export class ErrorService {

  constructor() { }

  setFieldError(form: FormGroup, fieldName: string, message: string) {
    const control = this.findFieldControl(form, fieldName);
    // TODO: find a better way to set the error message
    const errors = { [message]: true };
    control.setErrors(errors);
  }

  hasWrongValue(form: FormGroup, fieldName: string): boolean {
    return this.getFieldErrors(form, fieldName).length > 0;
  }

  // a field is correct only if it is filled and have no errors
  hasCorrectValue(form: FormGroup, fieldName: string): boolean {
    const control = this.findFieldControl(form, fieldName);
    // field found && user changed it && it doesn't hold a wrong value
    const isCorrect = control && !control.pristine && !this.hasWrongValue(form, fieldName);

    return isCorrect;
  }

  // right now we are rendering one error only for a field
  getFieldError(form: FormGroup, fieldName: string): string {
    return this.getFieldErrors(form, fieldName)[0];
  }

  getFieldErrors(form: FormGroup, fieldName: string): string[] {
    const control = this.findFieldControl(form, fieldName);
    if (control && control.touched && control.errors) {
      return this.getErrors(control);
    } else {
      return [];
    }
  }

  getErrors(control: AbstractControl): string[] {
    return Object.keys(control.errors)
      .filter((error: any) => control.errors[error])
      .map((error: any) => {
        const params = control.errors[error];
        return error;
      });
  }

  findFieldControl(form: FormGroup, fieldName: string): AbstractControl {
    return form.get(fieldName);
  }
}
