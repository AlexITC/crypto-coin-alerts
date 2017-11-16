package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.models._
import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.ws.WSClient

import scala.concurrent.Future

class BittrexAlertCollector @Inject() (bittrexClient: BittrexClient) extends TickerCollector {

  override val market: Market = Market.BITTREX

  override def getTickerList: Future[List[Ticker]] = {
    bittrexClient.getTickerList()
  }
}

class BittrexClient @Inject() (
    ws: WSClient)(
    implicit ec: TaskExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def getTickerList(): Future[List[Ticker]] = {
    val url = "https://bittrex.com/api/v1.1/public/getmarketsummaries"
    ws.url(url)
        .get()
        .map { response =>
          Option(response)
              .filter(_.status == 200)
              .map(_.json)
              .flatMap { json =>
                (json \ "success")
                    .asOpt[Boolean]
                    .filter(identity)
                    .flatMap { _ =>
                      (json \ "result")
                          .asOpt[List[Option[Ticker]]]
                          .map(_.flatten)
                    }
              }
              .getOrElse {
                logger.warn(s"Unexpected response from BITTREX, status = [${response.status}]")
                List.empty
              }
        }

  }

  implicit val tickerReads: Reads[Option[Ticker]] = {
    val builder = (JsPath \ "MarketName").read[String].map(createBook) and
        (JsPath \ "Last").read[BigDecimal]

    builder.apply { (bookMaybe, currentPrice) =>
        bookMaybe.map { book =>
          Ticker(book, currentPrice)
        }
    }
  }

  private def createBook(string: String): Option[Book] = {
    Book.fromString(string.replace("-", "_"))
        .orElse {
          logger.warn(s"Unable to create book from string = [$string]")
          None
        }
  }
}
