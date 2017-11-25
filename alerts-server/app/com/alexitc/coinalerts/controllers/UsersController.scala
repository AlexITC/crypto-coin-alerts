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

  def create() = publicWithInput(Created) { context: PublicRequestContextWithModel[CreateUserModel] =>
    userService.create(context.model)(context.lang)
  }

  def verifyEmail(token: UserVerificationToken) = publicNoInput { _: PublicRequestContext =>
    userService.verifyEmail(token)
  }

  def loginByEmail() = publicWithInput { context: PublicRequestContextWithModel[LoginByEmailModel] =>
    val loginModel = context.model
    userService.loginByEmail(loginModel.email, loginModel.password)
  }

  def whoAmI() = authenticatedNoInput { context: AuthenticatedRequestContext =>
    userService.userById(context.userId)
  }
}
