package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationErrors
import com.alexitc.coinalerts.commons.FutureOr.Implicits._
import com.alexitc.coinalerts.data.async.UserAsyncDAL
import com.alexitc.coinalerts.models.{CreateUserModel, User, UserHiddenPassword}
import com.alexitc.coinalerts.services.validators.UserValidator
import org.scalactic.Or

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject() (
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
    } yield user

    result.toFuture
  }
}
