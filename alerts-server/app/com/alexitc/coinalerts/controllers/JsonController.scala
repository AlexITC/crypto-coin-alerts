package com.alexitc.coinalerts.controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureOr.Implicits.{FutureOps, OrOps}
import com.alexitc.coinalerts.commons._
import com.alexitc.coinalerts.controllers.actions.LoggingAction
import com.alexitc.coinalerts.errors.{ConflictError, InputValidationError, JsonErrorRenderer, JsonFieldValidationError}
import com.alexitc.coinalerts.models.MessageKey
import org.scalactic.{Bad, Every, Good}
import play.api.i18n.Lang
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Base Controller designed to process actions that expect an input model
 * and computes an output model.
 *
 * The controller handles the json serialization and deserialization as well
 * as the error responses and http status codes.
 */
class JsonController extends MessagesBaseController {

  @Inject()
  protected var controllerComponents: MessagesControllerComponents = _

  @Inject()
  protected var loggingAction: LoggingAction = _

  @Inject()
  protected implicit var ec: ExecutionContext = _

  @Inject()
  protected var errorRenderer: JsonErrorRenderer = _

  /**
   * Execute an asynchronous action that receives the model [[R]]
   * and resurns the model [[M]] on success.
   *
   * @param block
   * @param tjs
   * @tparam R the input model type
   * @tparam M the output model type
   * @return
   */
  def async[R: Reads, M <: ModelDescription](
      block: R => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = loggingAction.async(parse.json) { request =>

    implicit val lang: Lang = messagesApi.preferred(request).lang

    val result = for {
      input <- validate[R](request.body).toFutureOr
      output <- block(input).toFutureOr
    } yield output

    toResult(result.toFuture)(lang, tjs)
  }

  private def validate[R: Reads](json: JsValue): ApplicationResult[R] = {
    json.validate[R].fold(
      invalid => {
        val errorList: Seq[JsonFieldValidationError] = invalid.map { case (path, errors) =>
          JsonFieldValidationError(
            path,
            errors
                .flatMap(_.messages)
                .map(MessageKey.apply))
        }

        // assume that errorList is non empty
        Bad(Every(errorList.head, errorList.drop(1): _*))
      },
      valid => Good(valid)
    )
  }

  // TODO: catch exceptions
  private def toResult[M <: ModelDescription](
      response: FutureApplicationResult[M])(
      implicit lang: Lang,
      tjs: Writes[M]): Future[Result] = response.map {

    case Good(value) =>
      renderSuccessfulResult(value)(tjs)

    case Bad(errors) =>
      renderErrors(errors)
  }

  private def renderSuccessfulResult[M <: ModelDescription](model: M)(implicit tjs: Writes[M]) = {
    val status = model match {
      case _: DataRetrieved => Results.Ok
      case _: ModelCreated => Results.Created
    }

    val json = Json.toJson(model)
    status.apply(json)
  }

  def renderErrors(errors: ApplicationErrors)(implicit lang: Lang): Result = {
    // detect response status based on the first error
    val status = errors.head match {
      case _: InputValidationError => Results.BadRequest
      case _: ConflictError => Results.Conflict
    }

    val jsonErrorList = errors
        .toList
        .flatMap(errorRenderer.toPublicErrorList)
        .map(errorRenderer.renderPublicError)

    val json = Json.obj("errors" -> jsonErrorList)

    status(Json.toJson(json))
  }
}


