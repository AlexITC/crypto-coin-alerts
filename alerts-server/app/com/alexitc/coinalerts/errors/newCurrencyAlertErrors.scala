package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.commons.{ConflictError, NotFoundError}

sealed trait NewCurrencyAlertError
case object RepeatedExchangeError extends NewCurrencyAlertError with ConflictError
case object NewCurrencyAlertNotFoundError extends NewCurrencyAlertError with NotFoundError
