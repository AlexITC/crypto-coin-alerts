package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.core.{Count, MessageKey}
import org.postgresql.util.PSQLException
import play.api.libs.json.JsPath

// Top-level errors
sealed trait ApplicationError
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
case class PostgresIntegrityViolationError(column: Option[String], cause: PSQLException) extends PostgresError

case class WrappedExceptionError(cause: Throwable) extends PrivateError

// play json validation errors
case class JsonFieldValidationError(path: JsPath, errors: Seq[MessageKey]) extends InputValidationError

// User errors
sealed trait UserError
case object InvalidEmailFormatError extends UserError with InputValidationError
case class InvalidEmailLengthError(maxLength: Int) extends UserError with InputValidationError
case class InvalidPasswordLengthError(validLength: Range) extends UserError with InputValidationError
case object EmailAlreadyExistsError extends UserError with ConflictError
case object VerifiedUserNotFound extends UserError with InputValidationError
case object IncorrectPasswordError extends UserError with InputValidationError

// Verify user email
sealed trait UserVerificationTokenError
case object UserVerificationTokenNotFoundError extends UserVerificationTokenError with NotFoundError
case object UserVerificationTokenAlreadyExistsError extends UserVerificationTokenError with ConflictError

// JWT
sealed trait JWTError extends ApplicationError
case object AuthorizationHeaderRequiredError extends JWTError with AuthenticationError
case object InvalidJWTError extends JWTError with AuthenticationError

// Mailgun
sealed trait MailgunError extends ApplicationError
case object MailgunSendEmailError extends MailgunError with InputValidationError

// Fixed price alert
sealed trait FixedPriceAlertError extends ApplicationError
case object InvalidPriceError extends FixedPriceAlertError with InputValidationError
case object InvalidBasePriceError extends FixedPriceAlertError with InputValidationError
case object FixedPriceAlertNotFoundError extends FixedPriceAlertError with NotFoundError
case class TooManyFixedPriceAlertsError(reachedLimit: Count) extends FixedPriceAlertError with ConflictError
case object InvalidFilterError extends FixedPriceAlertError with InputValidationError

// Paginated query
sealed trait PaginatedQueryError extends ApplicationError
case object InvalidQueryOffsetError extends PaginatedQueryError with InputValidationError
case class InvalidQueryLimitError(maxValue: Int) extends PaginatedQueryError with InputValidationError

// Daily price alert
sealed trait DailyPriceAlertError extends ApplicationError
case object RepeatedDailyPriceAlertError extends DailyPriceAlertError with ConflictError

sealed trait ExchangeCurrencyError extends ApplicationError
case object UnknownExchangeCurrencyIdError extends ExchangeCurrencyError with InputValidationError
case object RepeatedExchangeCurrencyError extends ExchangeCurrencyError with InputValidationError
case object ExchangeCurrencyNotFoundError extends ExchangeCurrencyError with NotFoundError

sealed trait ReCaptchaError extends ApplicationError
case object ReCaptchaValidationError extends ReCaptchaError with InputValidationError
