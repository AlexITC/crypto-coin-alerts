package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.commons.InputValidationError

// Mailgun
sealed trait MailgunError
case object MailgunSendEmailError extends MailgunError with InputValidationError
