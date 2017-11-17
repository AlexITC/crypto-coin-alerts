package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models._

import scala.language.higherKinds

trait UserDataHandler[F[_]] {

  def create(email: UserEmail, password: UserHiddenPassword): F[User]

  def createVerificationToken(userId: UserId): F[UserVerificationToken]

  def verifyEmail(token: UserVerificationToken): F[User]

  def getVerifiedUserPassword(email: UserEmail): F[UserHiddenPassword]

  def getVerifiedUserByEmail(email: UserEmail): F[User]

  def getVerifiedUserById(userId: UserId): F[User]
}

trait UserBlockingDataHandler extends UserDataHandler[ApplicationResult]
