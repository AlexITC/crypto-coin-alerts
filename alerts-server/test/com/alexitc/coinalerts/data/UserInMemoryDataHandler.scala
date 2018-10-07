package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.errors.{EmailAlreadyExistsError, UserVerificationTokenNotFoundError, VerifiedUserNotFound}
import com.alexitc.coinalerts.models._
import com.alexitc.playsonify.core.ApplicationResult
import org.scalactic.{Bad, Good, One, Or}

import scala.collection.mutable

trait UserInMemoryDataHandler extends UserBlockingDataHandler {

  val userList = mutable.ListBuffer[User]()
  val passwords = mutable.HashMap[UserEmail, UserHiddenPassword]()
  val verificationTokenList = mutable.ListBuffer[(UserId, UserVerificationToken)]()
  val verifiedUserList = mutable.ListBuffer[User]()
  val userPreferences = mutable.HashMap[UserId, UserPreferences]()

  override def create(email: UserEmail, password: UserHiddenPassword): ApplicationResult[User] = userList.synchronized {
    if (userList.exists(_.email.string.equalsIgnoreCase(email.string))) {
      Bad(EmailAlreadyExistsError).accumulating
    } else {
      val newUser = User(UserId.create, email)
      userList += newUser
      passwords += email -> password
      userPreferences += newUser.id -> UserPreferences.default(newUser.id)

      Good(newUser)
    }
  }

  override def createVerificationToken(userId: UserId): ApplicationResult[UserVerificationToken] =
    userList.synchronized {
      val token = UserVerificationToken.create(userId)
      verificationTokenList += userId -> token
      Good(token)
    }

  override def verifyEmail(token: UserVerificationToken): ApplicationResult[User] = userList.synchronized {
    val userMaybe = for {
      userId <- verificationTokenList.find(_._2 == token).map(_._1)
      user <- userList.find(_.id == userId)
    } yield {
      verifiedUserList += user
      user
    }

    Or.from(userMaybe, One(UserVerificationTokenNotFoundError))
  }

  override def getVerifiedUserPassword(email: UserEmail): ApplicationResult[UserHiddenPassword] =
    userList.synchronized {
      val passwordMaybe = passwords.get(email).filter(_ => verifiedUserList.exists(_.email == email))

      Or.from(passwordMaybe, One(VerifiedUserNotFound))
    }

  override def getVerifiedUserByEmail(email: UserEmail): ApplicationResult[User] = userList.synchronized {
    val userMaybe = verifiedUserList.find(_.email == email)

    Or.from(userMaybe, One(VerifiedUserNotFound))
  }

  override def getVerifiedUserById(userId: UserId): ApplicationResult[User] = userList.synchronized {
    val userMaybe = verifiedUserList.find(_.id == userId)

    Or.from(userMaybe, One(VerifiedUserNotFound))
  }

  override def getUserPreferences(userId: UserId): ApplicationResult[UserPreferences] = userList.synchronized {
    Good(UserPreferences.default(userId))
  }

  override def setUserPreferences(
      userId: UserId,
      preferencesModel: SetUserPreferencesModel): ApplicationResult[UserPreferences] = userList.synchronized {
    val preferencesMaybe = verifiedUserList
      .find(_.id == userId)
      .map { _ =>
        val preferences = UserPreferences.from(userId, preferencesModel)
        userPreferences += userId -> preferences

        preferences
      }

    Or.from(preferencesMaybe, One(VerifiedUserNotFound))
  }
}
