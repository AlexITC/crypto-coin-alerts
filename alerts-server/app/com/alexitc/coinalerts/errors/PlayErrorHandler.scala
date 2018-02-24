package com.alexitc.coinalerts.errors

import javax.inject.{Inject, Singleton}

import com.alexitc.coinalerts.commons.{ErrorId, PublicError, PublicErrorRenderer}
import org.slf4j.LoggerFactory
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent._

@Singleton
class PlayErrorHandler @Inject() (errorRenderer: PublicErrorRenderer) extends HttpErrorHandler {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    val publicError = PublicError.genericError(message)
    val error = errorRenderer.renderPublicError(publicError)
    val json = Json.obj(
      "errors" -> Json.arr(error)
    )

    val result = Status(statusCode)(json)
    Future.successful(result)
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    val errorId = ErrorId.create
    val error = errorRenderer.renderPrivateError(errorId)
    val json = Json.obj(
      "errors" -> Json.arr(error)
    )

    logger.error(s"Server error, errorId = [${errorId.string}]", exception)
    val result = InternalServerError(json)
    Future.successful(result)
  }
}
