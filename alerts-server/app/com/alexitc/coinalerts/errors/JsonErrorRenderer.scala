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
  }

  def toPublicErrorList(error: ApplicationError)(implicit lang: Lang): Seq[PublicError] = error match {
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

    case _: PrivateError => List.empty
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
}
