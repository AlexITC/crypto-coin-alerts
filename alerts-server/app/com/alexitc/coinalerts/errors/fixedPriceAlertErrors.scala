package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.commons.{ConflictError, InputValidationError, NotFoundError}
import com.alexitc.coinalerts.core.Count

// Fixed price alert
sealed trait FixedPriceAlertError
case object InvalidPriceError extends FixedPriceAlertError with InputValidationError
case object InvalidBasePriceError extends FixedPriceAlertError with InputValidationError
case object FixedPriceAlertNotFoundError extends FixedPriceAlertError with NotFoundError
case class TooManyFixedPriceAlertsError(reachedLimit: Count) extends FixedPriceAlertError with ConflictError
case object InvalidFilterError extends FixedPriceAlertError with InputValidationError
case object InvalidOrderError extends FixedPriceAlertError with InputValidationError
