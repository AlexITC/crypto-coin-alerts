package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.{InvalidEmailFormatError, InvalidEmailLengthError, InvalidPasswordLengthError}
import com.alexitc.coinalerts.models.{CreateUserModel, UserEmail, UserPassword}
import org.apache.commons.validator.routines.EmailValidator
import org.scalactic.Accumulation.withGood
import org.scalactic.{Bad, Good}

class UserValidator {

  def validateCreateUserModel(createUserModel: CreateUserModel): ApplicationResult[CreateUserModel] = {
    withGood(
      validateEmailFormat(createUserModel.email),
      validatePasswordFormat(createUserModel.password)) {

      CreateUserModel.apply
    }
  }

  private val MaxEmailLength = 64
  private val PasswordLengthRange = 8 to 30

  private def validateEmailFormat(email: UserEmail): ApplicationResult[UserEmail] = {
    val isValid = EmailValidator.getInstance().isValid(email.string)
    if (isValid) {
      if (email.string.length <= MaxEmailLength) {
        Good(email)
      } else {
        Bad(InvalidEmailLengthError(MaxEmailLength)).accumulating
      }
    } else {
      Bad(InvalidEmailFormatError).accumulating
    }
  }

  private def validatePasswordFormat(password: UserPassword): ApplicationResult[UserPassword] = {
    if (PasswordLengthRange.contains(password.string.length)) {
      // TODO: enforce strong password?
      Good(password)
    } else {
      Bad(InvalidPasswordLengthError(PasswordLengthRange)).accumulating
    }
  }
}
