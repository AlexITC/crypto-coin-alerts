package com.alexitc.coinalerts.controllers

import com.alexitc.coinalerts.data.{UserDAL, UserInMemoryDAL}
import org.scalatest.concurrent.ScalaFutures
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{FakeRequest, PlaySpecification}
import play.api.{Application, Mode}

class UsersControllerSpec extends PlaySpecification with ScalaFutures {

  val userDAL = new UserInMemoryDAL {}
  val application: Application = GuiceApplicationBuilder()
      .in(Mode.Test)
      .configure(inMemoryDatabase("test")) // TODO: remove usage of h2 and disable play db
      .overrides(bind[UserDAL].to(userDAL))
      .build()

  "POST /users" should {

    def createUserJson(email: String, password: String = "stupidpwd") = {
      s"""{ "email": "$email", "password": "$password"}"""
    }

    def callCreateUser(json: String) = {
      val request = FakeRequest(POST, "/users")
          .withHeaders(CONTENT_TYPE -> "application/json")
          .withBody(json)

      route(application, request).get
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
}
