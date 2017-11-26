package com.alexitc.coinalerts.models

import play.api.libs.functional.syntax._
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
  implicit val writes: Writes[FixedPriceAlert] = (
      (JsPath \ "id").write[FixedPriceAlertId] and
          (JsPath \ "market").write[Market] and
          (JsPath \ "book").write[Book] and
          (JsPath \ "isGreaterThan").write[Boolean] and
          (JsPath \ "price").write[BigDecimal] and
          (JsPath \ "basePrice").writeNullable[BigDecimal]
      ) { alert =>

    (alert.id, alert.market, alert.book, alert.isGreaterThan, alert.price, alert.basePrice)
  }
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

  implicit val reads: Reads[CreateFixedPriceAlertModel] = {
    val builder = (JsPath \ "market").read[Market] and
        (JsPath \ "book").read[Book] and
        (JsPath \ "isGreaterThan").read[Boolean] and
        (JsPath \ "price").read[BigDecimal] and
        (JsPath \ "basePrice").readNullable[BigDecimal]

    builder(CreateFixedPriceAlertModel.apply _)
  }
}
