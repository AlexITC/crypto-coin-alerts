package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureOr.Implicits._
import com.alexitc.coinalerts.commons.{ApplicationErrors, FutureApplicationResult}
import com.alexitc.coinalerts.data.async.UserAsyncDAL
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.validators.UserValidator
import org.scalactic.Or

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject() (
    emailService: EmailService,
    userAsyncDAL: UserAsyncDAL,
    userValidator: UserValidator)(implicit ec: ExecutionContext) {

  def create(createUserModel: CreateUserModel): Future[User Or ApplicationErrors] = {
    val result = for {
      validatedModel <- userValidator
          .validateCreateUserModel(createUserModel)
          .toFutureOr

      user <- userAsyncDAL
          .create(validatedModel.email, UserHiddenPassword.fromPassword(validatedModel.password))
          .toFutureOr

      token <- userAsyncDAL.createVerificationToken(user.id).toFutureOr

      _ <- emailService.sendVerificationToken(user.email, token).toFutureOr
    } yield user

    result.toFuture
  }

  def verifyEmail(token: UserVerificationToken): FutureApplicationResult[User] = {
    userAsyncDAL.verifyEmail(token)
  }
}
