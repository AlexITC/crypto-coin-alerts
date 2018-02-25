package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.InputValidationError

sealed trait MailgunError
case object MailgunSendEmailError extends MailgunError with InputValidationError
