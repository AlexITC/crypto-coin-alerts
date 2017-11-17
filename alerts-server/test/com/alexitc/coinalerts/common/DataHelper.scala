package com.alexitc.coinalerts.common

import com.alexitc.coinalerts.data.UserBlockingDataHandler
import com.alexitc.coinalerts.models.{User, UserEmail, UserHiddenPassword, UserPassword}

object DataHelper {

  def createVerifiedUser(
      email: UserEmail = RandomDataGenerator.email,
      password: UserPassword = RandomDataGenerator.password)(
      implicit userDataHandler: UserBlockingDataHandler): User = {

    val user = createUnverifiedUser(email, password)
    val token = userDataHandler.createVerificationToken(user.id).get
    userDataHandler.verifyEmail(token).get
  }

  def createUnverifiedUser(
      email: UserEmail = RandomDataGenerator.email,
      password: UserPassword = RandomDataGenerator.password)(
      implicit userDataHandler: UserBlockingDataHandler): User = {

    userDataHandler.create(email, UserHiddenPassword.fromPassword(password)).get
  }
}
