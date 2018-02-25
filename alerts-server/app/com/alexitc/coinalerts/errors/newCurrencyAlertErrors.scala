package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.{ConflictError, NotFoundError}

sealed trait NewCurrencyAlertError
case object RepeatedExchangeError extends NewCurrencyAlertError with ConflictError
case object NewCurrencyAlertNotFoundError extends NewCurrencyAlertError with NotFoundError
