package com.alexitc.coinalerts.controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons._
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.UserService
import com.alexitc.play.tracer.PlayRequestTracing

class UsersController @Inject() (
    components: JsonControllerComponents,
    userService: UserService)
    extends JsonController(components)
    with PlayRequestTracing {

  def create() = publicWithInput(Created) { context: PublicCtxModel[CreateUserModel] =>
    userService.create(context.model)(context.lang)
  }

  def verifyEmail(token: UserVerificationToken) = publicNoInput { _: PublicCtx =>
    userService.verifyEmail(token)
  }

  def loginByEmail() = publicWithInput { context: PublicCtxModel[LoginByEmailModel] =>
    val loginModel = context.model
    userService.loginByEmail(loginModel.email, loginModel.password)
  }

  def whoAmI() = authenticatedNoInput { context: AuthCtx =>
    userService.userById(context.userId)
  }
}
