package com.alexitc.coinalerts.models

import com.alexitc.coinalerts.core.WrappedInt

case class NewCurrencyAlert(
    id: NewCurrencyAlertId,
    userId: UserId,
    exchange: Exchange)

case class NewCurrencyAlertId(int: Int) extends AnyVal with WrappedInt
