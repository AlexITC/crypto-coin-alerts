package com.alexitc.coinalerts.commons.examples

import javax.inject.Inject

import com.alexitc.coinalerts.commons._
import com.alexitc.coinalerts.models.User
import org.scalactic.{Bad, Good, Many}

import scala.concurrent.Future

class PublicNoInputController @Inject() (cc: CustomControllerComponents) extends CustomJsonController(cc) {

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
