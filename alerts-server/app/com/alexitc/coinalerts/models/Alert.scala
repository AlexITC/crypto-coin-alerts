package com.alexitc.coinalerts.models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Alert(
    id: AlertId,
    alertType: AlertType,
    userId: UserId,
    market: Market,
    book: Book,
    isGreaterThan: Boolean,
    price: BigDecimal,
    basePrice: Option[BigDecimal] = None
)
object Alert {
  implicit val writes: Writes[Alert] = (
      (JsPath \ "id").write[AlertId] and
          (JsPath \ "alertType").write[AlertType] and
          (JsPath \ "market").write[Market] and
          (JsPath \ "book").write[Book] and
          (JsPath \ "isGreaterThan").write[Boolean] and
          (JsPath \ "price").write[BigDecimal] and
          (JsPath \ "basePrice").writeNullable[BigDecimal]
      ) { alert =>

    (alert.id, alert.alertType, alert.market, alert.book, alert.isGreaterThan, alert.price, alert.basePrice)
  }
}

case class BasePriceAlert(
    alertId: AlertId,
    basePrice: BigDecimal
)

case class AlertId(long: Long) extends AnyVal
object AlertId {

  implicit val writes: Writes[AlertId] = Writes[AlertId] { id => JsNumber(id.long) }
}

sealed abstract class AlertType(val string: String)
object AlertType {

  case object DEFAULT extends AlertType("DEFAULT")
  case object BASE_PRICE extends AlertType("BASE_PRICE")
  case class UNKNOWN(override val string: String) extends AlertType(string)

  private val fromStringPF: PartialFunction[String, AlertType] = {
    case DEFAULT.string => DEFAULT
    case BASE_PRICE.string => BASE_PRICE
  }

  def fromDatabaseString(string: String): AlertType = {
    if (fromStringPF.isDefinedAt(string))
      fromStringPF(string)
    else
      UNKNOWN(string)
  }

  implicit val reads: Reads[AlertType] = {
    JsPath.read[String].collect(JsonValidationError("error.alertType.unknown"))(fromStringPF)
  }

  implicit val writes: Writes[AlertType] = Writes[AlertType] { alertType => JsString(alertType.string) }
}
