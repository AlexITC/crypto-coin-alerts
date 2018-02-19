package com.alexitc.coinalerts.commons

import javax.inject.Inject

import com.alexitc.coinalerts.errors.IncorrectPasswordError
import com.alexitc.coinalerts.models.User
import org.scalactic.{Bad, Good, Many}
import play.api.libs.json.{Format, JsValue, Json}
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

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
      errorList.foreach { error =>
        val error = errorList.head
        (error \ "type").as[String] mustEqual "field-validation-error"
        (error \ "field").as[String] mustEqual "password"
        (error \ "message").as[String].nonEmpty mustEqual true
      }
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
      errorList.foreach { error =>
        val error = errorList.head
        (error \ "type").as[String] mustEqual "field-validation-error"
        (error \ "field").as[String] mustEqual "password"
        (error \ "message").as[String].nonEmpty mustEqual true
      }
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


class PublicWithInputController @Inject() (cc: JsonControllerComponents) extends AbstractJsonController(cc) {
  def getModel() = publicWithInput { context: PublicRequestContextWithModel[CustomModel] =>
    Future.successful(Good(context.model))
  }

  def getCustomStatus() = publicWithInput(Created) { context: PublicRequestContextWithModel[CustomModel] =>
    Future.successful(Good(context.model))
  }

  // TODO: Don't depend on app errors, allow to customize it
  def getErrors() = publicWithInput[CustomModel, CustomModel] { context: PublicRequestContextWithModel[CustomModel] =>
    val result = Bad(Many(IncorrectPasswordError, IncorrectPasswordError))
    Future.successful(result)
  }

  def getException(exception: Exception) = publicWithInput[CustomModel, CustomModel] { context: PublicRequestContextWithModel[CustomModel] =>
    Future.failed(exception)
  }
}

class PublicNoInputController @Inject() (cc: JsonControllerComponents) extends AbstractJsonController(cc) {
  def getModel(int: Int, string: String) = publicNoInput { context =>
    val result = CustomModel(int, string)
    Future.successful(Good(result))
  }

  def getCustomStatus() = publicNoInput(Created) { context =>
    val result = CustomModel(0, "no")
    Future.successful(Good(result))
  }

  def getErrors() = publicNoInput[User] { context: PublicRequestContext =>
      // TODO: Don't depend on app errors, allow to customize it
    val result = Bad(Many(IncorrectPasswordError, IncorrectPasswordError))
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