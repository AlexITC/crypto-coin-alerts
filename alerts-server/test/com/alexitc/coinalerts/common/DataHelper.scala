package com.alexitc.coinalerts.common

import com.alexitc.coinalerts.data.UserDAL
import com.alexitc.coinalerts.models.{User, UserEmail, UserHiddenPassword, UserPassword}

object DataHelper {

  def createVerifiedUser(
      email: UserEmail = RandomDataGenerator.email,
      password: UserPassword = RandomDataGenerator.password)(
      implicit userDAL: UserDAL): User = {

    val user = createUnverifiedUser(email, password)
    val token = userDAL.createVerificationToken(user.id).get
    userDAL.verifyEmail(token).get
  }

  def createUnverifiedUser(
      email: UserEmail = RandomDataGenerator.email,
      password: UserPassword = RandomDataGenerator.password)(
      implicit userDAL: UserDAL): User = {

    userDAL.create(email, UserHiddenPassword.fromPassword(password)).get
  }
}
