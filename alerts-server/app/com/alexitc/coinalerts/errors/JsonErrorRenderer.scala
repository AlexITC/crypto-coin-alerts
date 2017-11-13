package com.alexitc.coinalerts.errors

import javax.inject.Inject

import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{JsValue, Json}

class JsonErrorRenderer @Inject() (messagesApi: MessagesApi) {

  def renderPublicError(publicError: PublicError): JsValue = publicError match {
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

  def toPublicErrorList(error: ApplicationError)(implicit lang: Lang): Seq[PublicError] = error match {
    case _: PrivateError => List.empty

    case error: JWTError =>
      List(renderJWTError(error))

    case JsonFieldValidationError(path, errors) =>
      val field = path.path.map(_.toJsonString.replace(".", "")).mkString(".")
      errors.map { messageKey =>
        val message = messagesApi(messageKey.string)
        FieldValidationError(field, message)
      }

    case createUserError: CreateUserError =>
      List(renderCreateUserError(createUserError))

    case tokenError: UserVerificationTokenError =>
      List(renderUserVerificationTokenError(tokenError))

    case error: LoginByEmailError =>
      List(renderLoginByEmailError(error))

    case error: CreateAlertError =>
      List(renderCreateAlertError(error))
  }

  private def renderJWTError(jwtError: JWTError)(implicit lang: Lang) = jwtError match {
    case AuthorizationHeaderRequiredError =>
      val message = messagesApi("error.header.missing", "Authorization")
      HeaderValidationError("Authorization", message)

    case InvalidJWTError =>
      val message = messagesApi("error.jwt.invalid")
      HeaderValidationError("Authorization", message)
  }

  private def renderCreateUserError(createUserError: CreateUserError)(implicit lang: Lang) = createUserError match {
    case InvalidEmailLength(maxLength) =>
      val message = messagesApi("error.email.length", maxLength)
      FieldValidationError("email", message)

    case InvalidEmailFormat =>
      val message = messagesApi("error.email.format")
      FieldValidationError("email", message)

    case EmailAlreadyExists =>
      val message = messagesApi("error.email.conflict")
      FieldValidationError("email", message)

    case InvalidPasswordLength(range) =>
      val message = messagesApi("error.password.length", range.start, range.end)
      FieldValidationError("password", message)
  }

  private def renderUserVerificationTokenError(error: UserVerificationTokenError)(implicit lang: Lang) = error match {
    // NOTE: UserVerificationTokenNotFound is the only expected error
    case _: UserVerificationTokenError =>
      val message = messagesApi("error.token.verification")
      FieldValidationError("token", message)
  }

  private def renderLoginByEmailError(error: LoginByEmailError)(implicit lang: Lang) = error match {
    case VerifiedUserNotFound =>
      val message = messagesApi("error.verifiedUser.notFound")
      FieldValidationError("email", message)

    case IncorrectPasswordError =>
      val message = messagesApi("error.password.incorrect")
      FieldValidationError("password", message)
  }

  private def renderCreateAlertError(error: CreateAlertError)(implicit lang: Lang) = error match {
    case UnknownAlertTypeError =>
      val message = messagesApi("error.alertType.unknown")
      FieldValidationError("alertType", message)

    case InvalidPriceError =>
      val message = messagesApi("error.price.invalid")
      FieldValidationError("price", message)

    case InvalidBasePriceError =>
      val message = messagesApi("error.basePrice.invalid")
      FieldValidationError("basePrice", message)

    case BasePriceRequiredError =>
      val message = messagesApi("error.basePrice.required")
      FieldValidationError("basePrice", message)

    case BasePriceNotExpectedError =>
      val message = messagesApi("error.basePrice.notRequired")
      FieldValidationError("basePrice", message)
  }
}
