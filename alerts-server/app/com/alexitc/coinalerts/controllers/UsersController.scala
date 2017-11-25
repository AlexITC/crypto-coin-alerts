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

  def create() = unsecureAsync[CreateUserModel, User](Created) { context =>
    userService.create(context.model)(context.lang)
  }

  def verifyEmail(token: UserVerificationToken) = unsecureAsync { _: PublicRequestContext =>
    userService.verifyEmail(token)
  }

  def loginByEmail() = unsecureAsync { context: PublicRequestContextWithModel[LoginByEmailModel] =>
    val loginModel = context.model
    userService.loginByEmail(loginModel.email, loginModel.password)
  }

  def whoAmI() = async { context: AuthenticatedRequestContext =>
    userService.userById(context.userId)
  }
}
