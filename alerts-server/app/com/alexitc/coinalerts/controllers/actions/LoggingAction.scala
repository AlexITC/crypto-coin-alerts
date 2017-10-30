package com.alexitc.coinalerts.controllers.actions

import javax.inject.Inject

import com.alexitc.play.tracer.{PlayRequestId, PlayRequestTracerLoggerFactory}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LoggingAction @Inject() (
    parser: BodyParsers.Default,
    implicit val playRequestId: PlayRequestId,
    implicit val ec: ExecutionContext)
    extends ActionBuilderImpl(parser) {

  val logger = PlayRequestTracerLoggerFactory.getLogger(this.getClass)

  def apply[A](action: Action[A]) = {
    async(action.parser) { request =>
      action(request)
    }
  }

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    logger.info(s"${request.host} - ${request.method} ${request.uri} ${request.version} ${request.remoteAddress}")

    block(request)
  }
}
