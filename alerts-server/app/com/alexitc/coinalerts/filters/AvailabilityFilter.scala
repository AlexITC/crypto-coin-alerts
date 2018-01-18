package com.alexitc.coinalerts.filters

import javax.inject.Inject

import akka.stream.Materializer
import com.alexitc.coinalerts.core.ShutdownHandler
import org.slf4j.LoggerFactory
import play.api.mvc.{Filter, RequestHeader, Result, Results}

import scala.concurrent.Future

/**
 * Allow us to reject incoming requests when the application is shutting down.
 */
class AvailabilityFilter @Inject() (
    shutdownHandler: ShutdownHandler,
    implicit val mat: Materializer)
    extends Filter {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def apply(nextFilter: RequestHeader => Future[Result])
      (requestHeader: RequestHeader): Future[Result] = {

    if (shutdownHandler.isShuttingDown) {
      logger.info("Rejecting request because we are shutting down")
      Future.successful(Results.ServiceUnavailable)
    } else {
      nextFilter(requestHeader)
    }
  }
}