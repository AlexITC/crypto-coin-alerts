package com.alexitc.coinalerts.filters

import javax.inject.Inject

import akka.stream.Materializer
import org.slf4j.LoggerFactory
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      logger.info(
          s"${requestHeader.method} ${requestHeader.uri} took $requestTime ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}
