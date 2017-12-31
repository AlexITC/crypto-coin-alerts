package com.alexitc.coinalerts.tasks.models

import com.alexitc.coinalerts.models.{ExchangeCurrency, FixedPriceAlert}

case class FixedPriceAlertEvent(
    alert: FixedPriceAlert,
    exchangeCurrency: ExchangeCurrency,
    currentPrice: BigDecimal)
