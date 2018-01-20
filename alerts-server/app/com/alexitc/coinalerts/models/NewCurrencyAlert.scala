package com.alexitc.coinalerts.models

import com.alexitc.coinalerts.core.WrappedInt
import play.api.libs.json.{Json, Reads, Writes}

case class NewCurrencyAlert(
    id: NewCurrencyAlertId,
    userId: UserId,
    exchange: Exchange)
object NewCurrencyAlert {
  implicit val writes: Writes[NewCurrencyAlert] = Json.writes[NewCurrencyAlert]
}

case class NewCurrencyAlertId(int: Int) extends AnyVal with WrappedInt

case class CreateNewCurrencyAlertModel(exchange: Exchange)
object CreateNewCurrencyAlertModel {
  implicit val reads: Reads[CreateNewCurrencyAlertModel] = Json.reads
}
