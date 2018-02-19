package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.commons.{ConflictError, InputValidationError}

// User errors
sealed trait UserError
case object InvalidEmailFormatError extends UserError with InputValidationError
case class InvalidEmailLengthError(maxLength: Int) extends UserError with InputValidationError
case class InvalidPasswordLengthError(validLength: Range) extends UserError with InputValidationError
case object EmailAlreadyExistsError extends UserError with ConflictError
case object VerifiedUserNotFound extends UserError with InputValidationError
case object IncorrectPasswordError extends UserError with InputValidationError
case object UnsupportedLangError extends UserError with InputValidationError
