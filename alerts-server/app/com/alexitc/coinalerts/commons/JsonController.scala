package com.alexitc.coinalerts.commons

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureOr.Implicits.{FutureOps, OptionOps, OrOps}
import com.alexitc.coinalerts.errors._
import com.alexitc.coinalerts.models.{AuthorizationToken, ErrorId, MessageKey, UserId}
import org.scalactic.TypeCheckedTripleEquals._
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
 */
abstract class JsonController @Inject() (components: JsonControllerComponents)
    extends MessagesBaseController {

  protected def logger: org.slf4j.Logger

  protected implicit val ec = components.executionContext

  protected def controllerComponents: MessagesControllerComponents = components.messagesControllerComponents

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
      implicit tjs: Writes[M]): Action[JsValue] = components.loggingAction.async(parse.json) { request =>

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
      implicit tjs: Writes[M]): Action[JsValue] = components.loggingAction.async(EmptyJsonParser) { request =>

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
      block: AuthenticatedRequestContextWithModel[R] => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = components.loggingAction.async(parse.json) { request =>

    val lang = messagesApi.preferred(request).lang
    val result = for {
      authorizationHeader <- request.headers
          .get(AUTHORIZATION)
          .toFutureOr(InvalidJWTError)

      userId <- validateJWT(authorizationHeader).toFutureOr
      input <- validate[R](request.body).toFutureOr
      context = AuthenticatedRequestContextWithModel(userId, input, lang)
      output <- block(context).toFutureOr
    } yield output

    toResult(successStatus, result.toFuture)(lang, tjs)
  }

  /**
   * Sets a default successStatus.
   */
  def authenticatedWithInput[R: Reads, M](
      block: AuthenticatedRequestContextWithModel[R] => FutureApplicationResult[M])(
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
      block: AuthenticatedRequestContext => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = components.loggingAction.async(EmptyJsonParser) { request =>

    val lang = messagesApi.preferred(request).lang
    val result = for {
      authorizationHeader <- request.headers
          .get(AUTHORIZATION)
          .toFutureOr(InvalidJWTError)

      userId <- validateJWT(authorizationHeader).toFutureOr
      context = AuthenticatedRequestContext(userId, lang)
      output <- block(context).toFutureOr
    } yield output

    toResult(successStatus, result.toFuture)(lang, tjs)
  }

  /**
   * Sets a default successStatus.
   */
  def authenticatedNoInput[M](
      block: AuthenticatedRequestContext => FutureApplicationResult[M])(
      implicit tjs: Writes[M]): Action[JsValue] = {

    authenticatedNoInput[M](Ok)(block)
  }

  private def validateJWT(authorizationHeader: String): ApplicationResult[UserId] = {
    val tokenType = "Bearer"
    val headerParts = authorizationHeader.split(" ")

    Option(headerParts)
        .filter(_.length === 2)
        .filter(_.head === tokenType)
        .map(_.drop(1).head)
        .map(AuthorizationToken.apply)
        .map(components.jwtService.decodeToken)
        .getOrElse(Bad(InvalidJWTError).accumulating)
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
      case _: PrivateError => Results.InternalServerError
    }

    val json = errors.head match {
      case error: PrivateError =>
        val errorId = ErrorId.create
        logPrivateError(error, errorId)
        renderPrivateError(errorId)

      case _ => renderPublicErrors(errors)
    }
    status(Json.toJson(json))
  }

  private def renderPublicErrors(errors: ApplicationErrors)(implicit lang: Lang) = {
    val jsonErrorList = errors
        .toList
        .flatMap(components.errorRenderer.toPublicErrorList)
        .map(components.errorRenderer.renderPublicError)

    Json.obj("errors" -> jsonErrorList)
  }

  private def logPrivateError(error: PrivateError, errorId: ErrorId) = {
    logger.error(s"Unexpected internal error = ${errorId.string}", error.cause)
  }

  private def renderPrivateError(errorId: ErrorId) = {
    val jsonError = Json.obj(
      "type" -> "internal-error",
      "errorId" -> errorId.string
    )

    Json.obj("errors" -> List(jsonError))
  }
}
