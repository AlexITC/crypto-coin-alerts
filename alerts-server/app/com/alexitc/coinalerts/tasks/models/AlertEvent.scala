package com.alexitc.coinalerts.tasks.models

import com.alexitc.coinalerts.models.Alert

case class AlertEvent(
    alert: Alert,
    currentPrice: BigDecimal,
    basePrice: Option[BigDecimal])
