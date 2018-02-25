package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.ConflictError

sealed trait DailyPriceAlertError
case object RepeatedDailyPriceAlertError extends DailyPriceAlertError with ConflictError
