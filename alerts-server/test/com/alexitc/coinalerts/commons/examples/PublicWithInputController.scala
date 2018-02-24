package com.alexitc.coinalerts.commons.examples

import javax.inject.Inject

import com.alexitc.coinalerts.commons.PublicRequestContextWithModel
import org.scalactic.{Bad, Good, Many}

import scala.concurrent.Future

class PublicWithInputController @Inject() (cc: CustomControllerComponents) extends CustomJsonController(cc) {
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
