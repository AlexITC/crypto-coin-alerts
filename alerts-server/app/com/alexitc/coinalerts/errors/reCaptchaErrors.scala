package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.commons.InputValidationError

sealed trait ReCaptchaError
case object ReCaptchaValidationError extends ReCaptchaError with InputValidationError
