package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.models.MessageKey
import org.postgresql.util.PSQLException
import play.api.libs.json.JsPath

// Top-level errors
trait ApplicationError
sealed trait InputValidationError extends ApplicationError
sealed trait ConflictError extends ApplicationError
sealed trait NotFoundError extends ApplicationError
sealed trait AuthenticationError extends ApplicationError
sealed trait PrivateError extends ApplicationError {
  // contains data private to the server
  def cause: Throwable
}

// Exceptions

// PostgreSQL specific errors
sealed trait PostgresError extends PrivateError {
  def cause: PSQLException
}
case class PostgresIntegrityViolationError(cause: PSQLException) extends PostgresError

case class WrappedExceptionError(cause: Throwable) extends PrivateError

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

// login
sealed trait LoginByEmailError extends ApplicationError
case object VerifiedUserNotFound extends LoginByEmailError with InputValidationError
case object IncorrectPasswordError extends LoginByEmailError with InputValidationError

// JWT
sealed trait JWTError extends ApplicationError
case object AuthorizationHeaderRequiredError extends JWTError with AuthenticationError
case object InvalidJWTError extends JWTError with AuthenticationError
