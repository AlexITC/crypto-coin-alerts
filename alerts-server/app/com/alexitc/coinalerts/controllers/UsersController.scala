package com.alexitc.coinalerts.controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureOr.Implicits._
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.UserService
import com.alexitc.play.tracer.PlayRequestTracing

class UsersController @Inject() (
    components: JsonControllerComponents,
    userService: UserService)
    extends JsonController(components)
    with PlayRequestTracing {

  def create() = unsecureAsync { createUserModel: CreateUserModel =>
    val result = for {
      createdUser <- userService
          .create(createUserModel)
          .toFutureOr
    } yield UserCreatedModel(createdUser.id, createdUser.email)

    result.toFuture
  }

  def verifyEmail(token: UserVerificationToken) = unsecureAsync {
    userService.verifyEmail(token)
  }

  def loginByEmail() = unsecureAsync { loginModel: LoginByEmailModel =>
    userService.loginByEmail(loginModel.email, loginModel.password)
  }

  def whoAmI() = async { userId =>
    userService.userById(userId)
  }
}
