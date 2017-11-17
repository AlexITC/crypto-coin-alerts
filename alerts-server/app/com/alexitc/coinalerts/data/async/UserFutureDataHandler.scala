package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.config.DatabaseExecutionContext
import com.alexitc.coinalerts.data.{UserBlockingDataHandler, UserDataHandler}
import com.alexitc.coinalerts.models._

import scala.concurrent.Future

class UserFutureDataHandler @Inject() (
    userBlockingDataHandler: UserBlockingDataHandler)(
    implicit ec: DatabaseExecutionContext)
    extends UserDataHandler[FutureApplicationResult] {

  override def create(email: UserEmail, password: UserHiddenPassword): FutureApplicationResult[User] = Future {
    userBlockingDataHandler.create(email, password)
  }

  override def createVerificationToken(userId: UserId): FutureApplicationResult[UserVerificationToken] = Future {
    userBlockingDataHandler.createVerificationToken(userId)
  }

  override def verifyEmail(token: UserVerificationToken): FutureApplicationResult[User] = Future {
    userBlockingDataHandler.verifyEmail(token)
  }

  override def getVerifiedUserPassword(email: UserEmail): FutureApplicationResult[UserHiddenPassword] = Future {
    userBlockingDataHandler.getVerifiedUserPassword(email)
  }

  override def getVerifiedUserByEmail(email: UserEmail): FutureApplicationResult[User] = Future {
    userBlockingDataHandler.getVerifiedUserByEmail(email)
  }

  override def getVerifiedUserById(userId: UserId): FutureApplicationResult[User] = Future {
    userBlockingDataHandler.getVerifiedUserById(userId)
  }
}
