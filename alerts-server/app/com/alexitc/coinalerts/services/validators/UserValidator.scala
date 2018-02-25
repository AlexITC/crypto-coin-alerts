package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.errors.{InvalidEmailFormatError, InvalidEmailLengthError, InvalidPasswordLengthError, UnsupportedLangError}
import com.alexitc.coinalerts.models._
import com.alexitc.playsonify.core.ApplicationResult
import org.apache.commons.validator.routines.EmailValidator
import org.scalactic.Accumulation.withGood
import org.scalactic.{Bad, Good, One, Or}

class UserValidator {

  def validateCreateUserModel(createUserModel: CreateUserModel): ApplicationResult[CreateUserModel] = {
    withGood(
      validateEmailFormat(createUserModel.email),
      validatePasswordFormat(createUserModel.password)) { (_, _) =>

      createUserModel
    }
  }

  def validateSetUserPreferencesModel(preferencesModel: SetUserPreferencesModel): ApplicationResult[SetUserPreferencesModel] = {
    val preferencesMaybe = UserPreferences
        .AvailableLangs
        .find(_ == preferencesModel.lang)
        .map(_ => preferencesModel)

    Or.from(preferencesMaybe, One(UnsupportedLangError))
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
