package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.core.AuthorizationToken
import com.alexitc.coinalerts.data.async.UserFutureDataHandler
import com.alexitc.coinalerts.errors.IncorrectPasswordError
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.validators.UserValidator
import com.alexitc.playsonify.core.FutureOr.Implicits.{FutureOps, OrOps}
import com.alexitc.playsonify.core.{ApplicationErrors, FutureApplicationResult}
import org.mindrot.jbcrypt.BCrypt
import org.scalactic._
import play.api.i18n.Lang

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
    emailMessagesProvider: EmailMessagesProvider,
    emailService: EmailServiceTrait,
    userDataHandler: UserFutureDataHandler,
    userValidator: UserValidator,
    jwtService: JWTService)(implicit ec: ExecutionContext) {

  def create(createUserModel: CreateUserModel)(implicit lang: Lang): Future[User Or ApplicationErrors] = {
    val result = for {
      validatedModel <- userValidator
        .validateCreateUserModel(createUserModel)
        .toFutureOr

      user <- userDataHandler
        .create(validatedModel.email, UserHiddenPassword.fromPassword(validatedModel.password))
        .toFutureOr

      token <- userDataHandler.createVerificationToken(user.id).toFutureOr

      // send verification token by email
      _ <- emailService
        .sendEmail(user.email, emailMessagesProvider.verifyEmailSubject, emailMessagesProvider.verifyEmailText(token))
        .toFutureOr
    } yield user

    result.toFuture
  }

  def verifyEmail(token: UserVerificationToken): FutureApplicationResult[AuthorizationToken] = {
    val result = for {
      user <- userDataHandler.verifyEmail(token).toFutureOr
    } yield jwtService.createToken(user)

    result.toFuture
  }

  def loginByEmail(email: UserEmail, password: UserPassword): FutureApplicationResult[AuthorizationToken] = {
    val result = for {
      _ <- enforcePasswordMatches(email, password).toFutureOr
      user <- userDataHandler.getVerifiedUserByEmail(email).toFutureOr
    } yield jwtService.createToken(user)

    result.toFuture
  }

  def enforcePasswordMatches(email: UserEmail, password: UserPassword): FutureApplicationResult[Unit] = {
    val result = for {
      existingPassword <- userDataHandler.getVerifiedUserPassword(email).toFutureOr
      _ <- Good(BCrypt.checkpw(password.string, existingPassword.string)).filter { matches =>
        if (matches) Pass
        else Fail(One(IncorrectPasswordError))
      }.toFutureOr
    } yield ()

    result.toFuture
  }

  def userById(userId: UserId): FutureApplicationResult[User] = {
    userDataHandler.getVerifiedUserById(userId)
  }

  def getPreferences(userId: UserId): FutureApplicationResult[UserPreferences] = {
    userDataHandler.getUserPreferences(userId)
  }

  def setPreferences(
      userId: UserId,
      preferencesModel: SetUserPreferencesModel): FutureApplicationResult[UserPreferences] = {
    val result = for {
      validatedPreferences <- userValidator.validateSetUserPreferencesModel(preferencesModel).toFutureOr
      userPreferences <- userDataHandler.setUserPreferences(userId, validatedPreferences).toFutureOr
    } yield userPreferences

    result.toFuture
  }
}
