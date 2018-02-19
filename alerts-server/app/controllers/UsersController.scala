package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureOr.Implicits.FutureOps
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.UserService
import com.alexitc.coinalerts.services.external.ReCaptchaService

class UsersController @Inject() (
    reCaptchaService: ReCaptchaService,
    components: MyJsonControllerComponents,
    userService: UserService)
    extends MyJsonController(components) {

  def create() = publicWithInput(Created) { context: PublicCtxModel[CreateUserModel] =>
    val result = for {
      _ <- reCaptchaService.verify(context.model.reCaptchaResponse).toFutureOr
      createdUser <- userService.create(context.model)(context.lang).toFutureOr
    } yield createdUser

    result.toFuture
  }

  def verifyEmail(token: UserVerificationToken) = publicNoInput { _: PublicCtx =>
    userService.verifyEmail(token)
  }

  def loginByEmail() = publicWithInput { context: PublicCtxModel[LoginByEmailModel] =>
    val loginModel = context.model
    val result = for {
      _ <- reCaptchaService.verify(context.model.reCaptchaResponse).toFutureOr
      token <- userService.loginByEmail(loginModel.email, loginModel.password).toFutureOr
    } yield token

    result.toFuture
  }

  def whoAmI() = authenticatedNoInput { context: AuthCtx =>
    userService.userById(context.userId)
  }

  def getPreferences() = authenticatedNoInput { context =>
    userService.getPreferences(context.userId)
  }

  def setPreferences() = authenticatedWithInput { context: AuthCtxModel[SetUserPreferencesModel] =>
    userService.setPreferences(context.userId, context.model)
  }
}
