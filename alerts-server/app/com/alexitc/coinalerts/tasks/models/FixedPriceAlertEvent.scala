package com.alexitc.coinalerts.tasks.models

import com.alexitc.coinalerts.models.FixedPriceAlert

case class FixedPriceAlertEvent(
    alert: FixedPriceAlert,
    currentPrice: BigDecimal)
