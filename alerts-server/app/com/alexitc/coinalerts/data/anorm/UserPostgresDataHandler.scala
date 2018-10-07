package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.data.UserBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.dao.UserPostgresDAO
import com.alexitc.coinalerts.errors._
import com.alexitc.coinalerts.models._
import com.alexitc.playsonify.core.ApplicationResult
import org.scalactic.{Good, One, Or}
import org.slf4j.LoggerFactory
import play.api.db.Database

class UserPostgresDataHandler @Inject()(protected val database: Database, userDAO: UserPostgresDAO)
    extends UserBlockingDataHandler
    with AnormPostgresDAL {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def create(email: UserEmail, password: UserHiddenPassword): ApplicationResult[User] = withConnection {
    implicit conn =>
      val userMaybe = userDAO.create(email, password)
      userMaybe.foreach { user =>
        val preferences = SetUserPreferencesModel.default
        val _ = userDAO.setUserPreferences(user.id, preferences)
      }

      Or.from(userMaybe, One(EmailAlreadyExistsError))
  }

  override def createVerificationToken(userId: UserId): ApplicationResult[UserVerificationToken] = withConnection {
    implicit conn =>
      val tokenMaybe = userDAO.createVerificationToken(userId)
      Or.from(tokenMaybe, One(UserVerificationTokenAlreadyExistsError))
  }

  override def verifyEmail(token: UserVerificationToken): ApplicationResult[User] = withConnection { implicit conn =>
    val userMaybe = userDAO.verifyEmail(token)
    Or.from(userMaybe, One(UserVerificationTokenNotFoundError))
  }

  override def getVerifiedUserPassword(email: UserEmail): ApplicationResult[UserHiddenPassword] = withConnection {
    implicit conn =>
      val passwordMaybe = userDAO.getVerifiedUserPassword(email)
      Or.from(passwordMaybe, One(VerifiedUserNotFound))
  }

  override def getVerifiedUserByEmail(email: UserEmail): ApplicationResult[User] = withConnection { implicit conn =>
    val userMaybe = userDAO.getVerifiedUserByEmail(email)
    Or.from(userMaybe, One(VerifiedUserNotFound))
  }

  override def getVerifiedUserById(userId: UserId): ApplicationResult[User] = withConnection { implicit conn =>
    val userMaybe = userDAO.getVerifiedUserById(userId)
    Or.from(userMaybe, One(VerifiedUserNotFound))
  }

  /**
   * While every user should have preferences, we don't care if they are missing for some reason and return
   * default preferences instead.
   */
  override def getUserPreferences(userId: UserId): ApplicationResult[UserPreferences] = withConnection {
    implicit conn =>
      val userPreferences = userDAO
        .getUserPreferences(userId)
        .getOrElse {
          logger.warn(s"Preferences not found for user = [${userId.string}]")
          UserPreferences.default(userId)
        }

      Good(userPreferences)
  }

  override def setUserPreferences(
      userId: UserId,
      preferencesModel: SetUserPreferencesModel): ApplicationResult[UserPreferences] = {

    val result = withConnection { implicit conn =>
      val userPreferencesMaybe = userDAO.setUserPreferences(userId, preferencesModel)

      Or.from(userPreferencesMaybe, One(VerifiedUserNotFound))
    }

    result.badMap { errors =>
      errors.map {
        case PostgresIntegrityViolationError(Some("user_id"), _) => VerifiedUserNotFound
        case e => e
      }
    }
  }
}
