package com.alexitc.coinalerts.services.external

import javax.inject.Inject

import com.alexitc.coinalerts.config.ExternalServiceExecutionContext
import com.alexitc.coinalerts.models.{Book, Currency, Market}
import com.alexitc.coinalerts.tasks.models.Ticker
import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future

class HitbtcService @Inject() (
    ws: WSClient)(
    implicit ec: ExternalServiceExecutionContext)
    extends ExchangeService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val BaseURL = "https://api.hitbtc.com"

  /**
   * HitBTC gives us a book without a separator between the market and the currency,
   * for example: LTCBTC means BTC as market and LTC as currency.
   *
   * Then, we need to know the supported markets in advance to perform this parsing.
   */
  private val KnownMarkets = "BTC ETH USDT USD EOS DAI EURS".split(" ").flatMap(Market.from).toList

  override def availableBooks(): Future[List[Book]] = {
    getTickerList()
        .map { tickerList =>
          tickerList.map(_.book)
        }
  }

  override def getTickerList(): Future[List[Ticker]] = {
    val url = s"$BaseURL/api/2/public/ticker"

    ws.url(url)
        .get()
        .map { response =>
          Option(response)
              .flatMap(toJson)
              .flatMap { json =>
                json
                    .asOpt[List[Option[Ticker]]]
                    .map(_.flatten)
              }
              .getOrElse {
                logger.warn(s"Unexpected response from HITBTC, status = [${response.status}]")
                List.empty
              }
        }

  }

  private def toJson(response: WSResponse) = {
    if (response.status != 200) {
      None
    } else {
      val json = response.json
      Some(json)
    }
  }

  private implicit val tickerReads: Reads[Option[Ticker]] = {
    val builder = (JsPath \ "symbol").read[String].map(_.toUpperCase).map(createBook) and
        (JsPath \ "last").readNullable[BigDecimal]

    builder.apply { (bookMaybe, currentPriceMaybe) =>
      for (book <- bookMaybe; currentPrice <- currentPriceMaybe)
        yield Ticker(book, currentPrice)
    }
  }

  private def createBook(string: String): Option[Book] = {
    KnownMarkets
        .find { market => string.endsWith(market.string) }
        .flatMap { market =>
          val currencyStr = string.substring(0, string.length - market.string.length)
          for {
            currency <- Currency.from(currencyStr)
          } yield Book(market, currency)
        }
        .orElse {
          logger.warn(s"Unable to create book from string = [$string]")
          None
        }
  }
}