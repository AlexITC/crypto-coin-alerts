package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.data.UserDAL
import com.alexitc.coinalerts.models._

import scala.concurrent.Future

class UserAsyncDAL @Inject() (userDAL: UserDAL)(implicit ec: DatabaseExecutionContext) {

  def create(email: UserEmail, password: UserHiddenPassword): FutureApplicationResult[User] = Future {
    userDAL.create(email, password)
  }

  def createVerificationToken(userId: UserId): FutureApplicationResult[UserVerificationToken] = Future {
    userDAL.createVerificationToken(userId)
  }

  def verifyEmail(token: UserVerificationToken): FutureApplicationResult[User] = Future {
    userDAL.verifyEmail(token)
  }

  def getVerifiedUserPassword(email: UserEmail): FutureApplicationResult[UserHiddenPassword] = Future {
    userDAL.getVerifiedUserPassword(email)
  }

  def getVerifiedUserByEmail(email: UserEmail): FutureApplicationResult[User] = Future {
    userDAL.getVerifiedUserByEmail(email)
  }
}
