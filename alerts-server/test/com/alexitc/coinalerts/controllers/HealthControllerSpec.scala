package com.alexitc.coinalerts.controllers

import com.alexitc.coinalerts.commons.CustomPlayAPISpec
import play.api.Application
import play.api.test.Helpers._

class HealthControllerSpec extends CustomPlayAPISpec {

  val application: Application = guiceApplicationBuilder.build()

  "GET /health" should {
    "return OK" in {
      val response = GET("/health")

      status(response) mustEqual OK
    }
  }
}
