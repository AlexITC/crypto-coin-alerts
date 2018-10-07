package com.alexitc.coinalerts.services.external

import javax.inject.Inject

import com.alexitc.coinalerts.config.ExternalServiceExecutionContext
import com.alexitc.coinalerts.models.{Book, Currency, Market}
import com.alexitc.coinalerts.tasks.models.Ticker
import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Reads}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future

class BittrexService @Inject()(ws: WSClient)(implicit ec: ExternalServiceExecutionContext) extends ExchangeService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val BaseURL = "https://bittrex.com/api/v1.1/public"

  override def availableBooks(): Future[List[Book]] = {
    val url = s"$BaseURL/getmarkets"
    ws.url(url)
      .get()
      .map { response =>
        Option(response)
          .flatMap(toJson)
          .map { json =>
            val resultList = (json \ "result")
              .as[List[JsValue]]

            resultList.flatMap { result =>
              val marketMaybe = (result \ "BaseCurrency").asOpt[String].flatMap(Market.from)
              val currencyMaybe = (result \ "MarketCurrency").asOpt[String].flatMap(Currency.from)
              for (market <- marketMaybe; currency <- currencyMaybe)
                yield Book(market, currency)
            }
          }
          .getOrElse {
            logger.warn(s"Unexpected response from BITTREX, status = [${response.status}]")
            List.empty
          }
      }
  }

  override def getTickerList(): Future[List[Ticker]] = {
    val url = s"$BaseURL/getmarketsummaries"
    ws.url(url)
      .get()
      .map { response =>
        Option(response)
          .flatMap(toJson)
          .flatMap { json =>
            (json \ "result")
              .asOpt[List[Option[Ticker]]]
              .map(_.flatten)
          }
          .getOrElse {
            logger.warn(s"Unexpected response from BITTREX, status = [${response.status}]")
            List.empty
          }
      }

  }

  private def toJson(response: WSResponse) = {
    if (response.status != 200) {
      None
    } else {
      val json = response.json
      (json \ "success")
        .asOpt[Boolean]
        .filter(identity)
        .map(_ => json)
    }
  }

  private implicit val tickerReads: Reads[Option[Ticker]] = {
    val builder = (JsPath \ "MarketName").read[String].map(createBook) and
      (JsPath \ "Last").read[BigDecimal]

    builder.apply { (bookMaybe, currentPrice) =>
      bookMaybe.map { book =>
        Ticker(book, currentPrice)
      }
    }
  }

  private def createBook(string: String): Option[Book] = {
    Book
      .fromString(string.replace("-", "_"))
      .orElse {
        logger.warn(s"Unable to create book from string = [$string]")
        None
      }
  }
}
