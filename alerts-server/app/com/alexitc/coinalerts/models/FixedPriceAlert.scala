package com.alexitc.coinalerts.models

import play.api.libs.json._

case class FixedPriceAlert(
    id: FixedPriceAlertId,
    userId: UserId,
    market: Market,
    book: Book,
    isGreaterThan: Boolean,
    price: BigDecimal,
    basePrice: Option[BigDecimal] = None
)
object FixedPriceAlert {
  implicit val writes: Writes[FixedPriceAlert] = Json.writes[FixedPriceAlert]
}

case class FixedPriceAlertId(long: Long) extends AnyVal
object FixedPriceAlertId {

  implicit val writes: Writes[FixedPriceAlertId] = Writes[FixedPriceAlertId] { id => JsNumber(id.long) }
}

case class CreateFixedPriceAlertModel(
    market: Market,
    book: Book,
    isGreaterThan: Boolean,
    price: BigDecimal,
    basePrice: Option[BigDecimal]
)
object CreateFixedPriceAlertModel {

  implicit val reads: Reads[CreateFixedPriceAlertModel] = Json.reads[CreateFixedPriceAlertModel]
}
