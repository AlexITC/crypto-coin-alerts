package com.alexitc.coinalerts.errors

import javax.inject.Inject

import com.alexitc.coinalerts.commons._
import play.api.i18n.{Lang, MessagesApi}

class MyApplicationErrorMapper @Inject() (messagesApi: MessagesApi) extends ApplicationErrorMapper {

  override def toPublicErrorList(error: ApplicationError)(implicit lang: Lang): Seq[PublicError] = error match {
    case _: ServerError => List.empty

    case error: JWTError =>
      List(renderJWTError(error))

    case error: MailgunError =>
      renderMailgunError(error)

    case error: ReCaptchaError =>
      List(renderReCaptchaError(error))

    // TODO: avoid requiring this core mapper here
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

    case error: NewCurrencyAlertError =>
      List(renderNewCurrencyAlertError(error))
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

    case UnsupportedLangError =>
      val message = messagesApi("error.lang.incorrect")
      FieldValidationError("lang", message)
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

    case InvalidFilterError =>
      val message = messagesApi("error.fixedPriceAlert.invalidFilters")
      FieldValidationError("filter", message)

    case InvalidOrderError =>
      val message = messagesApi("error.fixedPriceAlert.invalidOrder")
      FieldValidationError("orderBy", message)
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

  private def renderNewCurrencyAlertError(error: NewCurrencyAlertError)(implicit lang: Lang) = error match {
    case RepeatedExchangeError =>
      val message = messagesApi("error.newCurrencyAlert.repeatedExchange")
      FieldValidationError("exchange", message)

    case NewCurrencyAlertNotFoundError =>
      val message = messagesApi("error.newCurrencyAlert.notFound")
      FieldValidationError("exchange", message)
  }
}
