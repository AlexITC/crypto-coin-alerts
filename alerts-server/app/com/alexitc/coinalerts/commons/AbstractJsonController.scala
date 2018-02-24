package com.alexitc.coinalerts.commons

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureOr.Implicits.{FutureOps, OrOps}
import com.alexitc.coinalerts.core.{ErrorId, MessageKey}
import com.alexitc.coinalerts.errors._
import org.scalactic.{Bad, Every, Good}
import play.api.i18n.Lang
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future
import scala.util.control.NonFatal

/**
 * Base Controller designed to process actions that expect an input model
 * and computes an output model.
 *
 * The controller handles the json serialization and deserialization as well
 * as the error responses and http status codes.
 *
 * @tparam A the value type for an authenticated request, like User or UserId.
 */
abstract class AbstractJsonController[A] @Inject() (components: JsonControllerComponents[A])
    extends MessagesBaseController {

  override protected val controllerComponents: MessagesControllerComponents = components.messagesControllerComponents

  protected implicit val ec = components.executionContext

  /**
   * Override this and decide what to do in case of server errors.
   *
   * For example, log the error with the id, handle metrics, etc.
   *
   * @param error the error that occurred.
   * @param errorId the unique identifier for the error.
   */
  protected def onServerError(error: ServerError, errorId: ErrorId): Unit

  /**
   * Ignores the body returning an empty json.
   *
   * Useful for using methods that doesn't require input.
   */
  private val EmptyJsonParser = parse.ignore(Json.toJson(JsObject.empty))

  /**
   * Execute an asynchronous action that receives the model [[R]]
   * and returns the model [[M]] on success.
   *
   * The model [[R]] is wrapped in a [[RequestContext]].
   *
   * Note: This method is intended to be used on public APIs.
   *
   * @param successStatus the http status for a successful response
   * @param block the block to execute
   * @param tjs the serializer for [[M]]
   * @tparam R the input model type
   * @tparam M the output model type
   */
  def publicWithInput[R: Reads, M](
      successStatus: Status)(
      block: PublicRequestContextWithModel[R] => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = Action.async(parse.json) { request =>

    val result = for {
      input <- validate[R](request.body).toFutureOr
      context = PublicRequestContextWithModel(input, messagesApi.preferred(request).lang)
      output <- block(context).toFutureOr
    } yield output

    val lang = messagesApi.preferred(request).lang
    toResult(successStatus, result.toFuture)(lang, tjs)
  }

  /**
   * Sets a default successStatus.
   */
  def publicWithInput[R: Reads, M](
      block: PublicRequestContextWithModel[R] => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = {

    publicWithInput[R, M](Ok)(block)
  }

  /**
   * Execute an asynchronous action that doesn't need an input model
   * and returns the model [[M]] on success.
   *
   * Note: This method is intended to be used on public APIs.
   *
   * @param successStatus the http status for a successful response
   * @param block the block to execute
   * @param tjs the serializer for [[M]]
   * @tparam M the output model type
   */
  def publicNoInput[M](
      successStatus: Status)(
      block: PublicRequestContext => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = Action.async(EmptyJsonParser) { request =>

    val context = PublicRequestContext(messagesApi.preferred(request).lang)
    val result = block(context)
    val lang = messagesApi.preferred(request).lang
    toResult(successStatus, result)(lang, tjs)
  }

  /**
   * Sets a default successStatus.
   */
  def publicNoInput[M](
      block: PublicRequestContext => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = {

    publicNoInput[M](Ok)(block)
  }

  /**
   * Execute an asynchronous action that receives the model [[R]]
   * and produces the model [[M]] on success, the http status in
   * case of a successful result will be taken from successStatus param.
   *
   * Note: This method is intended to be on APIs requiring authentication.
   *
   * @param successStatus the http status for a successful response
   * @param block the block to execute
   * @param tjs the serializer for [[M]]
   * @tparam R the input model type
   * @tparam M the output model type
   */
  def authenticatedWithInput[R: Reads, M](
      successStatus: Status)(
      block: AuthenticatedRequestContextWithModel[A, R] => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = Action.async(parse.json) { request =>

    val lang = messagesApi.preferred(request).lang
    val result = for {
      input <- validate[R](request.body).toFutureOr
      authValue <- components.authenticatorService.authenticate(request).toFutureOr
      context = AuthenticatedRequestContextWithModel(authValue, input, lang)
      output <- block(context).toFutureOr
    } yield output

    toResult(successStatus, result.toFuture)(lang, tjs)
  }

  /**
   * Sets a default successStatus.
   */
  def authenticatedWithInput[R: Reads, M](
      block: AuthenticatedRequestContextWithModel[A, R] => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = {

    authenticatedWithInput[R, M](Ok)(block)
  }

  /**
   * Execute an asynchronous action that doesn't need an input model
   * and returns the model [[M]] on success.
   *
   * Note: This method is intended to be on APIs requiring authentication.
   *
   * @param successStatus the http status for a successful response
   * @param block the block to execute
   * @param tjs the serializer for [[M]]
   * @tparam M the output model type
   */
  def authenticatedNoInput[M](
      successStatus: Status)(
      block: AuthenticatedRequestContext[A] => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = Action.async(EmptyJsonParser) { request =>

    val lang = messagesApi.preferred(request).lang
    val result = for {
      authValue <- components.authenticatorService.authenticate(request).toFutureOr
      context = AuthenticatedRequestContext(authValue, lang)
      output <- block(context).toFutureOr
    } yield output

    toResult(successStatus, result.toFuture)(lang, tjs)
  }

  /**
   * Sets a default successStatus.
   */
  def authenticatedNoInput[M](
      block: AuthenticatedRequestContext[A] => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = {

    authenticatedNoInput[M](Ok)(block)
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

  private def toResult[M](
      successStatus: Status,
      response: FutureApplicationResult[M])(
      implicit lang: Lang,
      tjs: Writes[M]): Future[Result] = {

    response.map {
      case Good(value) =>
        renderSuccessfulResult(successStatus, value)(tjs)

      case Bad(errors) =>
        renderErrors(errors)
    }.recover {
      case NonFatal(ex) =>
        val error = WrappedExceptionError(ex)
        renderErrors(Every(error))
    }
  }

  private def renderSuccessfulResult[M](successStatus: Status, model: M)(implicit tjs: Writes[M]) = {
    val json = Json.toJson(model)
    successStatus.apply(json)
  }

  private def renderErrors(errors: ApplicationErrors)(implicit lang: Lang): Result = {
    // detect response status based on the first error
    val status = errors.head match {
      case _: InputValidationError => Results.BadRequest
      case _: ConflictError => Results.Conflict
      case _: NotFoundError => Results.NotFound
      case _: AuthenticationError => Results.Unauthorized
      case _: ServerError => Results.InternalServerError
    }

    val json = errors.head match {
      case error: ServerError =>
        val errorId = ErrorId.create
        onServerError(error, errorId)
        renderPrivateError(errorId)

      case _ => renderPublicErrors(errors)
    }
    status(Json.toJson(json))
  }

  private def renderPublicErrors(errors: ApplicationErrors)(implicit lang: Lang) = {
    val jsonErrorList = errors
        .toList
        .flatMap(components.applicationErrorMapper.toPublicErrorList)
        .map(components.publicErrorRenderer.renderPublicError)

    Json.obj("errors" -> jsonErrorList)
  }

  private def renderPrivateError(errorId: ErrorId) = {
    val jsonError = components.publicErrorRenderer.renderPrivateError(errorId)

    Json.obj("errors" -> List(jsonError))
  }
}
