package com.alexitc.coinalerts.commons

import javax.inject.Inject

import com.alexitc.coinalerts.models.User
import com.alexitc.coinalerts.services.JWTService
import org.scalactic.{Bad, Good, Many}
import play.api.i18n.Lang
import play.api.libs.json.{Format, JsValue, Json}
import play.api.mvc.MessagesControllerComponents
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.{ExecutionContext, Future}

class AbstractJsonControllerSpec extends PlayAPISpec {

  val application = guiceApplicationBuilder.build()
  implicit val materializer = application.materializer

  val injector = application.injector

  "publicNoInput" should {
    val controller = injector.instanceOf[PublicNoInputController]

    "serialize a result as json" in {
      val int = 0
      val string = "hi"
      val result = controller.getModel(int, string).apply(FakeRequest())

      status(result) mustEqual OK
      val json = contentAsJson(result)
      (json \ "int").as[Int] mustEqual int
      (json \ "string").as[String] mustEqual string
    }

    "allows to override successful result status" in {
      val result = controller.getCustomStatus().apply(FakeRequest())

      status(result) mustEqual CREATED
    }

    "serializes an error list as json" in {
      val result = controller.getErrors().apply(FakeRequest())
      status(result) mustEqual BAD_REQUEST

      val json = contentAsJson(result)
      val errorList = (json \ "errors").as[List[JsValue]]

      errorList.size mustEqual 2

      val firstError = errorList.head
      (firstError \ "type").as[String] mustEqual "field-validation-error"
      (firstError \ "field").as[String] mustEqual "field"
      (firstError \ "message").as[String].nonEmpty mustEqual true

      val secondError = errorList.lift(1).get
      (secondError \ "type").as[String] mustEqual "field-validation-error"
      (secondError \ "field").as[String] mustEqual "anotherField"
      (secondError \ "message").as[String].nonEmpty mustEqual true
    }

    "serialize exceptions as json with an error id" in {
      val exception = new RuntimeException("failed")
      val result = controller.getException(exception).apply(FakeRequest())
      status(result) mustEqual INTERNAL_SERVER_ERROR

      val json = contentAsJson(result)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.size mustEqual 1

      val error = errorList.head
      (error \ "type").as[String] mustEqual "server-error"
      (error \ "errorId").as[String].nonEmpty mustEqual true
    }
  }

  "publicWithInput" should {
    val controller = injector.instanceOf[PublicWithInputController]

    "serialize a result as json" in {
      val body =
        """
          | {
          |   "int": 0,
          |   "string": "none"
          | }
        """.stripMargin
      val request = FakeRequest("POST", "/")
          .withJsonBody(Json.parse(body))
          .withHeaders(CONTENT_TYPE -> "application/json")
      val result = call(controller.getModel(), request)

      status(result) mustEqual OK
      val json = contentAsJson(result)
      (json \ "int").as[Int] mustEqual 0
      (json \ "string").as[String] mustEqual "none"
    }

    "reject invalid json body" in {
      val body =
        """
          | {
          |   int: 0
          |   string: "none"
          | }
        """.stripMargin
      val request = FakeRequest("POST", "/")
          .withBody(body)
          .withHeaders(CONTENT_TYPE -> "application/json")
      val result = call(controller.getModel(), request)

      status(result) mustEqual BAD_REQUEST
      val json = contentAsJson(result)
      val errors = (json \ "errors").as[List[JsValue]]
      errors.size mustEqual 1

      val error = errors.head
      (error \ "type").as[String] mustEqual "generic-error"
      (error \ "message").as[String].nonEmpty mustEqual true
    }

    "reject json with missing field" in {
      val body =
        """
          | {
          |   "string": "none"
          | }
        """.stripMargin
      val request = FakeRequest("POST", "/")
          .withBody(body)
          .withHeaders(CONTENT_TYPE -> "application/json")
      val result = call(controller.getModel(), request)

      status(result) mustEqual BAD_REQUEST
      val json = contentAsJson(result)
      val errors = (json \ "errors").as[List[JsValue]]
      errors.size mustEqual 1

      val error = errors.head
      (error \ "type").as[String] mustEqual "field-validation-error"
      (error \ "field").as[String] mustEqual "int"
      (error \ "message").as[String].nonEmpty mustEqual true
    }

    "reject json with wrong types" in {
      val body =
        """
          | {
          |   "int": "1",
          |   "string": "none"
          | }
        """.stripMargin
      val request = FakeRequest("POST", "/")
          .withBody(body)
          .withHeaders(CONTENT_TYPE -> "application/json")
      val result = call(controller.getModel(), request)

      status(result) mustEqual BAD_REQUEST
      val json = contentAsJson(result)

      val errors = (json \ "errors").as[List[JsValue]]
      errors.size mustEqual 1

      val error = errors.head
      (error \ "type").as[String] mustEqual "field-validation-error"
      (error \ "field").as[String] mustEqual "int"
      (error \ "message").as[String].nonEmpty mustEqual true
    }

    "reject empty body" in {
      val request = FakeRequest("POST", "/")
          .withHeaders(CONTENT_TYPE -> "application/json")
      val result = call(controller.getModel(), request)

      status(result) mustEqual BAD_REQUEST
      val json = contentAsJson(result)
      val errors = (json \ "errors").as[List[JsValue]]
      errors.size mustEqual 1

      val error = errors.head
      (error \ "type").as[String] mustEqual "generic-error"
      (error \ "message").as[String].nonEmpty mustEqual true
    }

    "allows to override successful result status" in {
      val body =
        """
          | {
          |   "int": 0,
          |   "string": "none"
          | }
        """.stripMargin
      val request = FakeRequest("POST", "/")
          .withJsonBody(Json.parse(body))
          .withHeaders(CONTENT_TYPE -> "application/json")
      val result = call(controller.getCustomStatus(), request)

      status(result) mustEqual CREATED
    }

    "serializes an error list as json" in {
      val body =
        """
          | {
          |   "int": 0,
          |   "string": "none"
          | }
        """.stripMargin
      val request = FakeRequest("POST", "/")
          .withJsonBody(Json.parse(body))
          .withHeaders(CONTENT_TYPE -> "application/json")
      val result = call(controller.getErrors(), request)
      status(result) mustEqual BAD_REQUEST

      val json = contentAsJson(result)
      val errorList = (json \ "errors").as[List[JsValue]]

      errorList.size mustEqual 2

      val firstError = errorList.head
      (firstError \ "type").as[String] mustEqual "field-validation-error"
      (firstError \ "field").as[String] mustEqual "field"
      (firstError \ "message").as[String].nonEmpty mustEqual true

      val secondError = errorList.lift(1).get
      (secondError \ "type").as[String] mustEqual "field-validation-error"
      (secondError \ "field").as[String] mustEqual "anotherField"
      (secondError \ "message").as[String].nonEmpty mustEqual true
    }

    "serialize exceptions as json with an error id" in {
      val body =
        """
          | {
          |   "int": 0,
          |   "string": "none"
          | }
        """.stripMargin
      val request = FakeRequest("POST", "/")
          .withJsonBody(Json.parse(body))
          .withHeaders(CONTENT_TYPE -> "application/json")
      val exception = new RuntimeException("nothing")
      val result = call(controller.getException(exception), request)
      status(result) mustEqual INTERNAL_SERVER_ERROR

      val json = contentAsJson(result)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.size mustEqual 1

      val error = errorList.head
      (error \ "type").as[String] mustEqual "server-error"
      (error \ "errorId").as[String].nonEmpty mustEqual true
    }
  }
}

class CustomErrorMapper extends ApplicationErrorMapper {
  import CustomErrorMapper._
  override def toPublicErrorList(applicationError: ApplicationError)(implicit lang: Lang): Seq[PublicError] = applicationError match {
    case JsonFieldValidationError(path, errors) =>
      val field = path.path.map(_.toJsonString.replace(".", "")).mkString(".")
      errors.map { messageKey =>
        val message = messageKey.string
        FieldValidationError(field, message)
      }

    case InputError =>
      val publicError = FieldValidationError("field", "just an error")
      List(publicError)

    case DuplicateError =>
      val publicError = FieldValidationError("anotherField", "just another error")
      List(publicError)
  }
}

object CustomErrorMapper {
  sealed trait CustomError
  case object InputError extends CustomError with InputValidationError
  case object DuplicateError extends CustomError with ConflictError
}

class CustomComponents @Inject()(
    override val messagesControllerComponents: MessagesControllerComponents,
    override val jwtService: JWTService,
    override val executionContext: ExecutionContext,
    override val publicErrorRenderer: PublicErrorRenderer,
    override val applicationErrorMapper: CustomErrorMapper)
    extends JsonControllerComponents


class PublicWithInputController @Inject() (cc: CustomComponents) extends AbstractJsonController(cc) {
  def getModel() = publicWithInput { context: PublicRequestContextWithModel[CustomModel] =>
    Future.successful(Good(context.model))
  }

  def getCustomStatus() = publicWithInput(Created) { context: PublicRequestContextWithModel[CustomModel] =>
    Future.successful(Good(context.model))
  }

  def getErrors() = publicWithInput[CustomModel, CustomModel] { context: PublicRequestContextWithModel[CustomModel] =>
    val result = Bad(Many(CustomErrorMapper.InputError, CustomErrorMapper.DuplicateError))
    Future.successful(result)
  }

  def getException(exception: Exception) = publicWithInput[CustomModel, CustomModel] { context: PublicRequestContextWithModel[CustomModel] =>
    Future.failed(exception)
  }
}

class PublicNoInputController @Inject() (cc: CustomComponents) extends AbstractJsonController(cc) {

  def getModel(int: Int, string: String) = publicNoInput { context =>
    val result = CustomModel(int, string)
    Future.successful(Good(result))
  }

  def getCustomStatus() = publicNoInput(Created) { context =>
    val result = CustomModel(0, "no")
    Future.successful(Good(result))
  }

  def getErrors() = publicNoInput[User] { context: PublicRequestContext =>
    val result = Bad(Many(CustomErrorMapper.InputError, CustomErrorMapper.DuplicateError))
    Future.successful(result)
  }

  def getException(exception: Exception) = publicNoInput[User] { context: PublicRequestContext =>
    Future.failed(exception)
  }
}

case class CustomModel(int: Int, string: String)
object CustomModel {
  implicit val format: Format[CustomModel] = Json.format[CustomModel]
}