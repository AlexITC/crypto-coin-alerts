package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.commons.examples.{AuthenticatedNoInputController, PublicNoInputController, PublicWithInputController}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import play.api.test._

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

  "authenticatedNoInput" should {
    val controller = injector.instanceOf[AuthenticatedNoInputController]

    "return UNAUTHORIZED when no AUTHORIZATION header is present" in {
      val result = controller.getModel(0, "").apply(FakeRequest())

      status(result) mustEqual UNAUTHORIZED
      val json = contentAsJson(result)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.size mustEqual 1

      val jsonError = errorList.head
      (jsonError \ "type").as[String] mustEqual "header-validation-error"
      (jsonError \ "header").as[String] mustEqual AUTHORIZATION
      (jsonError \ "message").as[String].nonEmpty mustEqual true
    }

    "return OK when the request is authenticated" in {
      val request = FakeRequest().withHeaders(AUTHORIZATION -> "user")
      val result = controller.getModel(0, "").apply(request)

      status(result) mustEqual OK
    }
  }
}
