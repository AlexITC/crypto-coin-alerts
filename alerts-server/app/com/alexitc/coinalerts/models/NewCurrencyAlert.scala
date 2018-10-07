package com.alexitc.coinalerts.models

import com.alexitc.coinalerts.models.NewCurrencyAlert.NewCurrencyAlertId
import com.alexitc.playsonify.models.WrappedInt
import play.api.libs.json.{Json, Reads, Writes}

case class NewCurrencyAlert(id: NewCurrencyAlertId, userId: UserId, exchange: Exchange)
object NewCurrencyAlert {
  implicit val writes: Writes[NewCurrencyAlert] = Json.writes[NewCurrencyAlert]
  case class NewCurrencyAlertId(int: Int) extends AnyVal with WrappedInt
}
