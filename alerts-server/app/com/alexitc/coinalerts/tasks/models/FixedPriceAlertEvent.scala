package com.alexitc.coinalerts.tasks.models

import com.alexitc.coinalerts.models.FixedPriceAlertWithCurrency

case class FixedPriceAlertEvent(
    alert: FixedPriceAlertWithCurrency,
    currentPrice: BigDecimal)
