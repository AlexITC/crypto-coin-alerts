package com.alexitc.coinalerts.models

import com.alexitc.coinalerts.core.WrappedLong
import play.api.libs.json._

case class FixedPriceAlert(
    id: FixedPriceAlertId,
    userId: UserId,
    currencyId: ExchangeCurrencyId,
    isGreaterThan: Boolean,
    price: BigDecimal,
    basePrice: Option[BigDecimal] = None
)
object FixedPriceAlert {
  implicit val writes: Writes[FixedPriceAlert] = Json.writes[FixedPriceAlert]
}

case class FixedPriceAlertId(long: Long) extends AnyVal with WrappedLong

case class CreateFixedPriceAlertModel(
    exchangeCurrencyId: ExchangeCurrencyId,
    isGreaterThan: Boolean,
    price: BigDecimal,
    basePrice: Option[BigDecimal]
)
object CreateFixedPriceAlertModel {

  implicit val reads: Reads[CreateFixedPriceAlertModel] = Json.reads[CreateFixedPriceAlertModel]
}
