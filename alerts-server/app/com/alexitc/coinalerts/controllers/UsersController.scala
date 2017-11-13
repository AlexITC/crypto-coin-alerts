package com.alexitc.coinalerts.controllers

import javax.inject.Inject

import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.UserService
import com.alexitc.play.tracer.PlayRequestTracing

class UsersController @Inject() (
    components: JsonControllerComponents,
    userService: UserService)
    extends JsonController(components)
    with PlayRequestTracing {

  def create() = unsecureAsync[CreateUserModel, User](Created) { createUserModel =>
    userService.create(createUserModel)
  }

  def verifyEmail(token: UserVerificationToken) = unsecureAsync[User](Ok) {
    userService.verifyEmail(token)
  }

  def loginByEmail() = unsecureAsync[LoginByEmailModel, AuthorizationToken](Ok) { loginModel =>
    userService.loginByEmail(loginModel.email, loginModel.password)
  }

  def whoAmI() = async[User](Ok) { userId =>
    userService.userById(userId)
  }
}
