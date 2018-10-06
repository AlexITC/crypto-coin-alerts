package com.alexitc.coinalerts.models

import java.time.OffsetDateTime

import com.alexitc.playsonify.models.WrappedLong
import play.api.libs.json._

case class DailyPriceAlert(
    id: DailyPriceAlertId,
    userId: UserId,
    exchangeCurrencyId: ExchangeCurrencyId,
    createdOn: OffsetDateTime)

object DailyPriceAlert {
  implicit val writes: Writes[DailyPriceAlert] = Json.writes[DailyPriceAlert]
}

case class DailyPriceAlertId(long: Long) extends AnyVal with WrappedLong

case class CreateDailyPriceAlertModel(exchangeCurrencyId: ExchangeCurrencyId)
object CreateDailyPriceAlertModel {
  implicit val reads: Reads[CreateDailyPriceAlertModel] = Json.reads[CreateDailyPriceAlertModel]
}
