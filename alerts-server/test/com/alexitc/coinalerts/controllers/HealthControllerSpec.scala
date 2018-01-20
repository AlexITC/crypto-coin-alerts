package com.alexitc.coinalerts.controllers

import com.alexitc.coinalerts.commons.PlayAPISpec
import play.api.Application
import play.api.test.Helpers._

class HealthControllerSpec extends PlayAPISpec {

  val application: Application = guiceApplicationBuilder.build()

  "GET /health" should {
    "return OK" in {
      val response = GET("/health")

      status(response) mustEqual OK
    }
  }
}
