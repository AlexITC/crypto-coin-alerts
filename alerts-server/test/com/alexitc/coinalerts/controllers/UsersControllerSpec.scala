package com.alexitc.coinalerts.controllers

import com.alexitc.coinalerts.commons.PlayAPISpec.AuthorizationTokenExt
import com.alexitc.coinalerts.commons.{DataHelper, PlayAPISpec, RandomDataGenerator}
import com.alexitc.coinalerts.models._
import play.api.Application
import play.api.libs.json.JsValue
import play.api.test.Helpers._

class UsersControllerSpec extends PlayAPISpec {

  val application: Application = guiceApplicationBuilder.build()

  "POST /users" should {

    def createUserJson(email: String, password: String = "stupidpwd") = {
      s"""{ "email": "$email", "password": "$password", "reCaptchaResponse": "none"}"""
    }

    def callCreateUser(json: String) = {
      POST("/users", Some(json))
    }

    "Allow to create a new user" in {
      val email = "email@domain.com"
      val response = callCreateUser(createUserJson(email))
      status(response) mustEqual CREATED

      val json = contentAsJson(response)
      (json \ "id").as[String] must not be empty
      (json \ "email").as[String] mustEqual email
    }

    "Return CONFLICT when the email is already registered" in {
      val email = "conflict@domain.com"
      var response = callCreateUser(createUserJson(email))
      status(response) mustEqual CREATED

      response = callCreateUser(createUserJson(email))
      status(response) mustEqual CONFLICT
    }

    "Retrun BAD REQUEST when the email or password are invalid" in {
      val validEmail = "valid@domain.com"
      val invalidEmail = "notanEmail@nothing"
      var response = callCreateUser(createUserJson(invalidEmail))
      status(response) mustEqual BAD_REQUEST

      response = callCreateUser(createUserJson(validEmail, "short"))
      status(response) mustEqual BAD_REQUEST
    }

    "Return BAD REQUEST when the input JSON is malformed" in {
      val invalidJson = """ }{" """
      val response = callCreateUser(invalidJson)
      status(response) mustEqual BAD_REQUEST
    }
  }

  "POST /users/verify-email/:token" should {
    def verifyEmailUrl(token: UserVerificationToken) = {
      s"/users/verify-email/${token.string}"
    }
    "Allow to verify a user based on the token" in {
      val email = RandomDataGenerator.email
      val user = userDataHandler.create(email, RandomDataGenerator.hiddenPassword).get
      val token = userDataHandler.createVerificationToken(user.id).get
      val response = POST(verifyEmailUrl(token))

      status(response) mustEqual OK
    }
  }

  "POST /users/login" should {
    val LoginUrl = "/users/login"

    "Allow to login with correct credentials" in {
      val email = RandomDataGenerator.email
      val password = RandomDataGenerator.password
      val user = DataHelper.createVerifiedUser(email, password)

      val json =
        s"""
          |{ "email": "${email.string}", "password": "${password.string}", "reCaptchaResponse": "none" }
        """.stripMargin

      val response = POST(LoginUrl, Some(json))
      status(response) mustEqual OK
    }

    "Fail to login an unverified user" in {
      val email = RandomDataGenerator.email
      val password = RandomDataGenerator.password
      val _ = userDataHandler.create(email, UserHiddenPassword.fromPassword(password)).get

      val json =
        s"""
           |{ "email": "${email.string}", "password": "${password.string}", "reCaptchaResponse": "none" }
        """.stripMargin
      val response = POST(LoginUrl, Some(json))
      status(response) mustEqual BAD_REQUEST
    }

    "Fail to login with incorrect password" in {
      val json =
        s"""
           |{ "email": "who@none.com", "password": "hmmm", "reCaptchaResponse": "none" }
        """.stripMargin

      val response = POST(LoginUrl, Some(json))
      status(response) mustEqual BAD_REQUEST
    }
  }

  "GET /users/me" should {

    val url = "/users/me"
    "Retrieve info from the logged in user" in {
      val email = RandomDataGenerator.email
      val password = RandomDataGenerator.password
      val user = DataHelper.createVerifiedUser(email, password)
      val token = jwtService.createToken(user)
      val response = GET(url, token.toHeader)
      status(response) mustEqual OK
      (contentAsJson(response) \ "id").as[String] mustEqual user.id.string
    }

    "Fail when Authorization token is not present" in {
      val response = GET(url)
      status(response) mustEqual UNAUTHORIZED
    }

    "Fail when the token is not of type = Bearer" in {
      val header = "OAuth Xys"
      val response = GET(url, AUTHORIZATION -> header)
      status(response) mustEqual UNAUTHORIZED
    }

    "Fail when the token is incorrect" in {
      val header = "Bearer Xys"
      val response = GET(url, AUTHORIZATION -> header)
      status(response) mustEqual UNAUTHORIZED
    }
  }

  "GET /users/me/preferences" should {
    val url = "/users/me/preferences"

    "retrieve the preferences" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)

      val response = GET(url, token.toHeader)
      status(response) mustEqual OK

      val json = contentAsJson(response)
      (json \ "userId").as[String] mustEqual user.id.string
      (json \ "lang").as[String] mustEqual "en"
    }
  }

  "PUT /users/me/preferences" should {
    val url = "/users/me/preferences"

    "update the preferences" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)
      val lang = "en"
      val body =
        s"""
          |{ "lang": "$lang" }
        """.stripMargin

      val response = PUT(url, Some(body), token.toHeader)
      status(response) mustEqual OK

      val json = contentAsJson(response)
      (json \ "userId").as[String] mustEqual user.id.string
      (json \ "lang").as[String] mustEqual lang
    }

    "fail to set an unsupported language" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)
      val lang = "ru"
      val body =
        s"""
           |{ "lang": "$lang" }
        """.stripMargin

      val response = PUT(url, Some(body), token.toHeader)
      status(response) mustEqual BAD_REQUEST

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.size mustEqual 1

      val error = errorList.head
      (error \ "type").as[String] mustEqual "field-validation-error"
      (error \ "field").as[String] mustEqual "lang"
      (error \ "message").as[String].nonEmpty mustEqual true
    }
  }
}
