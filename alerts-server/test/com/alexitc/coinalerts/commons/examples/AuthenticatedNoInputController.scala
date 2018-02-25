package com.alexitc.coinalerts.commons.examples

import javax.inject.Inject

import org.scalactic.Good

import scala.concurrent.Future

class AuthenticatedNoInputController @Inject() (cc: CustomControllerComponents) extends CustomJsonController(cc) {

  def getModel(int: Int, string: String) = authenticatedNoInput { context =>
    val result = CustomModel(int, string)
    Future.successful(Good(result))
  }
}
