package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.models._
import com.alexitc.playsonify.core.ApplicationResult

import scala.language.higherKinds

trait UserDataHandler[F[_]] {

  def create(email: UserEmail, password: UserHiddenPassword): F[User]

  def createVerificationToken(userId: UserId): F[UserVerificationToken]

  def verifyEmail(token: UserVerificationToken): F[User]

  def getVerifiedUserPassword(email: UserEmail): F[UserHiddenPassword]

  def getVerifiedUserByEmail(email: UserEmail): F[User]

  def getVerifiedUserById(userId: UserId): F[User]

  def getUserPreferences(userId: UserId): F[UserPreferences]

  def setUserPreferences(userId: UserId, preferencesModel: SetUserPreferencesModel): F[UserPreferences]
}

trait UserBlockingDataHandler extends UserDataHandler[ApplicationResult]
