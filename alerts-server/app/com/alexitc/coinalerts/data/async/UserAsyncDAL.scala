package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.data.UserDAL
import com.alexitc.coinalerts.models.{DatabaseExecutionContext, User, UserEmail, UserHiddenPassword}

import scala.concurrent.Future

class UserAsyncDAL @Inject() (userDAL: UserDAL)(implicit ec: DatabaseExecutionContext) {

  def create(email: UserEmail, password: UserHiddenPassword): FutureApplicationResult[User] = Future {
    userDAL.create(email, password)
  }
}
