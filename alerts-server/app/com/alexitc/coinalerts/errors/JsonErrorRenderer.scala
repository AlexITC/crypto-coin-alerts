package com.alexitc.coinalerts.errors

import javax.inject.Inject

import com.alexitc.coinalerts.core.ErrorId
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{JsValue, Json}

class JsonErrorRenderer @Inject() (messagesApi: MessagesApi) {

  def renderPublicError(publicError: PublicError): JsValue = publicError match {
    case e: GenericPublicError =>
      val obj = Json.obj(
        "type" -> "generic-error",
        "message" -> e.message
      )
      Json.toJson(obj)

    case e: FieldValidationError =>
      val obj = Json.obj(
        "type" -> "field-validation-error",
        "field" -> e.field,
        "message" -> e.message
      )
      Json.toJson(obj)

    case e: HeaderValidationError =>
      val obj = Json.obj(
        "type" -> "header-validation-error",
        "header" -> e.header,
        "message" -> e.message
      )
      Json.toJson(obj)
  }

  def renderPrivateError(errorId: ErrorId) = {
    Json.obj(
      "type" -> "server-error",
      "errorId" -> errorId.string
    )
  }

  def toPublicError(message: String): PublicError = {
    GenericPublicError(message)
  }

  def toPublicErrorList(error: ApplicationError)(implicit lang: Lang): Seq[PublicError] = error match {
    case _: PrivateError => List.empty

    case error: JWTError =>
      List(renderJWTError(error))

    case error: MailgunError =>
      renderMailgunError(error)

    case error: ReCaptchaError =>
      List(renderReCaptchaError(error))

    case JsonFieldValidationError(path, errors) =>
      val field = path.path.map(_.toJsonString.replace(".", "")).mkString(".")
      errors.map { messageKey =>
        val message = messagesApi(messageKey.string)
        FieldValidationError(field, message)
      }

    case error: UserError =>
      List(renderUserError(error))

    case error: UserVerificationTokenError =>
      List(renderUserVerificationTokenError(error))

    case error: FixedPriceAlertError =>
      List(renderFixedPriceAlertError(error))

    case error: PaginatedQueryError =>
      List(renderPaginatedQueryError(error))

    case error: DailyPriceAlertError =>
      List(renderDailyPriceAlertError(error))

    case error: ExchangeCurrencyError =>
      List(renderExchangeCurrencyError(error))
  }

  private def renderJWTError(jwtError: JWTError)(implicit lang: Lang) = jwtError match {
    case AuthorizationHeaderRequiredError =>
      val message = messagesApi("error.header.missing", "Authorization")
      HeaderValidationError("Authorization", message)

    case InvalidJWTError =>
      val message = messagesApi("error.jwt.invalid")
      HeaderValidationError("Authorization", message)
  }

  private def renderMailgunError(error: MailgunError)(implicit lang: Lang) = error match {
    case MailgunSendEmailError =>
      // this error should not happen
      List.empty[PublicError]
  }

  private def renderReCaptchaError(error: ReCaptchaError)(implicit lang: Lang) = error match {
    case ReCaptchaValidationError =>
      val message = messagesApi("error.recaptcha")
      GenericPublicError(message)
  }

  private def renderUserError(error: UserError)(implicit lang: Lang) = error match {
    case InvalidEmailLengthError(maxLength) =>
      val message = messagesApi("error.email.length", maxLength)
      FieldValidationError("email", message)

    case InvalidEmailFormatError =>
      val message = messagesApi("error.email.format")
      FieldValidationError("email", message)

    case EmailAlreadyExistsError =>
      val message = messagesApi("error.email.conflict")
      FieldValidationError("email", message)

    case InvalidPasswordLengthError(range) =>
      val message = messagesApi("error.password.length", range.start, range.end)
      FieldValidationError("password", message)

    case VerifiedUserNotFound =>
      val message = messagesApi("error.verifiedUser.notFound")
      FieldValidationError("email", message)

    case IncorrectPasswordError =>
      val message = messagesApi("error.password.incorrect")
      FieldValidationError("password", message)
  }

  private def renderUserVerificationTokenError(error: UserVerificationTokenError)(implicit lang: Lang) = error match {
    // NOTE: UserVerificationTokenNotFound is the only expected error
    case _: UserVerificationTokenError =>
      val message = messagesApi("error.token.verification")
      FieldValidationError("token", message)
  }

  private def renderFixedPriceAlertError(error: FixedPriceAlertError)(implicit lang: Lang) = error match {
    case InvalidPriceError =>
      val message = messagesApi("error.price.invalid")
      FieldValidationError("price", message)

    case InvalidBasePriceError =>
      val message = messagesApi("error.basePrice.invalid")
      FieldValidationError("basePrice", message)

    case FixedPriceAlertNotFoundError =>
      val message = messagesApi("error.fixedPriceAlert.notFound")
      FieldValidationError("fixedPriceAlertId", message)

    case TooManyFixedPriceAlertsError(reachedLimit) =>
      val message = messagesApi("error.fixedPriceAlert.limitReached", reachedLimit.int)
      GenericPublicError(message)
  }

  private def renderPaginatedQueryError(error: PaginatedQueryError)(implicit lang: Lang) = error match {
    case InvalidQueryOffsetError =>
      val message = messagesApi("error.paginatedQuery.offset.invalid")
      FieldValidationError("offset", message)

    case InvalidQueryLimitError(maxValue) =>
      val message = messagesApi("error.paginatedQuery.limit.invalid", maxValue)
      FieldValidationError("limit", message)
  }

  private def renderDailyPriceAlertError(error: DailyPriceAlertError)(implicit lang: Lang) = error match {
    case RepeatedDailyPriceAlertError =>
      val message = messagesApi("error.createDailyPriceAlert.repeated")
      FieldValidationError("exchangeCurrencyId", message)
  }

  private def renderExchangeCurrencyError(error: ExchangeCurrencyError)(implicit lang: Lang) = error match {
    case UnknownExchangeCurrencyIdError =>
      val message = messagesApi("error.exchangeCurrencyId.unknown")
      FieldValidationError("exchangeCurrencyId", message)

    case RepeatedExchangeCurrencyError =>
      val message = messagesApi("error.exchangeCurrency.repeated")
      FieldValidationError("currency", message)

    case ExchangeCurrencyNotFoundError =>
      val message = messagesApi("error.exchangeCurrency.notFound")
      FieldValidationError("currency", message)
  }
}
