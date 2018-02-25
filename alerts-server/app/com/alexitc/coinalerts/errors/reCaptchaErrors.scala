package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.InputValidationError

sealed trait ReCaptchaError
case object ReCaptchaValidationError extends ReCaptchaError with InputValidationError
