package com.alexitc.coinalerts.models

import com.alexitc.coinalerts.core.WrappedLong
import play.api.libs.json._

case class FixedPriceAlert(
    id: FixedPriceAlertId,
    userId: UserId,
    exchangeCurrencyId: ExchangeCurrencyId,
    isGreaterThan: Boolean,
    price: BigDecimal,
    basePrice: Option[BigDecimal] = None
)

/**
 * While we are can retrieve the exchange currency data using the API,
 * copying the data here simplifies a lot the front-end code to avoid
 * making a lot of calls to retrieve the currency data, this might
 * not be a good idea and could be updated.
 */
case class FixedPriceAlertWithCurrency(
    id: FixedPriceAlertId,
    userId: UserId,
    exchangeCurrencyId: ExchangeCurrencyId,
    exchange: Exchange,
    market: Market,
    currency: Currency,
    isGreaterThan: Boolean,
    price: BigDecimal,
    basePrice: Option[BigDecimal] = None
)
object FixedPriceAlertWithCurrency {

  def from(fixedPriceAlert: FixedPriceAlert, exchangeCurrency: ExchangeCurrency): FixedPriceAlertWithCurrency = {
    FixedPriceAlertWithCurrency(
      fixedPriceAlert.id,
      fixedPriceAlert.userId,
      exchangeCurrency.id,
      exchangeCurrency.exchange,
      exchangeCurrency.market,
      exchangeCurrency.currency,
      fixedPriceAlert.isGreaterThan,
      fixedPriceAlert.price,
      fixedPriceAlert.basePrice
    )
  }

  implicit val writes: Writes[FixedPriceAlertWithCurrency] = Json.writes[FixedPriceAlertWithCurrency]
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
