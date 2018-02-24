package com.alexitc.coinalerts.commons

import play.api.libs.json.JsPath

sealed trait JsonControllerErrors

// play json validation errors
case class JsonFieldValidationError(path: JsPath, errors: Seq[MessageKey]) extends JsonControllerErrors with InputValidationError
