package com.alexitc.coinalerts.commons.examples

import com.alexitc.coinalerts.commons._
import play.api.i18n.Lang

class CustomErrorMapper extends ApplicationErrorMapper {
  import CustomErrorMapper._
  override def toPublicErrorList(applicationError: ApplicationError)(implicit lang: Lang): Seq[PublicError] = applicationError match {
    case JsonFieldValidationError(path, errors) =>
      val field = path.path.map(_.toJsonString.replace(".", "")).mkString(".")
      errors.map { messageKey =>
        val message = messageKey.string
        FieldValidationError(field, message)
      }

    case InputError =>
      val publicError = FieldValidationError("field", "just an error")
      List(publicError)

    case DuplicateError =>
      val publicError = FieldValidationError("anotherField", "just another error")
      List(publicError)

    case FailedAuthError =>
      val publicError = HeaderValidationError("Authorization", "Invalid auth")
      List(publicError)
  }
}

object CustomErrorMapper {
  sealed trait CustomError
  case object InputError extends CustomError with InputValidationError
  case object DuplicateError extends CustomError with ConflictError
  case object FailedAuthError extends CustomError with AuthenticationError
}
