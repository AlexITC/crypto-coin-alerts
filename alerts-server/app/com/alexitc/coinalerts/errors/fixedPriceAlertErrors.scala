package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.core.Count
import com.alexitc.playsonify.models.{ConflictError, InputValidationError, NotFoundError}

// Fixed price alert
sealed trait FixedPriceAlertError
case object InvalidPriceError extends FixedPriceAlertError with InputValidationError
case object InvalidBasePriceError extends FixedPriceAlertError with InputValidationError
case object FixedPriceAlertNotFoundError extends FixedPriceAlertError with NotFoundError
case class TooManyFixedPriceAlertsError(reachedLimit: Count) extends FixedPriceAlertError with ConflictError
case object InvalidFilterError extends FixedPriceAlertError with InputValidationError
case object InvalidOrderError extends FixedPriceAlertError with InputValidationError
