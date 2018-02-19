package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.core.MessageKey
import play.api.libs.json.JsPath

sealed trait JsonControllerErrors

// play json validation errors
case class JsonFieldValidationError(path: JsPath, errors: Seq[MessageKey]) extends JsonControllerErrors with InputValidationError
