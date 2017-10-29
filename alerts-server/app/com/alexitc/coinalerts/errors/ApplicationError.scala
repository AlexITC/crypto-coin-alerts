package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.models.MessageKey
import play.api.libs.json.JsPath

// Top-level errors
trait ApplicationError
sealed trait InputValidationError extends ApplicationError
sealed trait ConflictError extends ApplicationError

// play json validation errors
case class JsonFieldValidationError(path: JsPath, errors: Seq[MessageKey]) extends InputValidationError
