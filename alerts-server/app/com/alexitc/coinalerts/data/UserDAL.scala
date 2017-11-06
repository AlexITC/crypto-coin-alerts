package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models._

trait UserDAL {

  def create(email: UserEmail, password: UserHiddenPassword): ApplicationResult[User]

  def createVerificationToken(userId: UserId): ApplicationResult[UserVerificationToken]

  def verifyEmail(token: UserVerificationToken): ApplicationResult[User]

  def getVerifiedUserPassword(email: UserEmail): ApplicationResult[UserHiddenPassword]

  def getVerifiedUserByEmail(email: UserEmail): ApplicationResult[User]

  def getVerifiedUserById(userId: UserId): ApplicationResult[User]
}
