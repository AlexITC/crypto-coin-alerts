package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.models.MessageKey
import play.api.libs.json.JsPath

// Top-level errors
trait ApplicationError
sealed trait InputValidationError extends ApplicationError
sealed trait ConflictError extends ApplicationError
sealed trait NotFoundError extends ApplicationError

// play json validation errors
case class JsonFieldValidationError(path: JsPath, errors: Seq[MessageKey]) extends InputValidationError

// Create user errors
sealed trait CreateUserError
case object InvalidEmailFormat extends CreateUserError with InputValidationError
case class InvalidEmailLength(maxLength: Int) extends CreateUserError with InputValidationError
case class InvalidPasswordLength(validLength: Range) extends CreateUserError with InputValidationError
case object EmailAlreadyExists extends CreateUserError with ConflictError

// Verify user email
sealed trait UserVerificationTokenError
case object UserVerificationTokenNotFound extends UserVerificationTokenError with NotFoundError
case object UserVerificationTokenAlreadyExists extends UserVerificationTokenError with ConflictError
