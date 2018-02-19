package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.commons.ConflictError

// Daily price alert
sealed trait DailyPriceAlertError
case object RepeatedDailyPriceAlertError extends DailyPriceAlertError with ConflictError
