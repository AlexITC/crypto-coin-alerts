package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.data.UserBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.dao.UserPostgresDAO
import com.alexitc.coinalerts.errors.{EmailAlreadyExists, UserVerificationTokenAlreadyExists, UserVerificationTokenNotFound, VerifiedUserNotFound}
import com.alexitc.coinalerts.models._
import org.scalactic.{One, Or}
import play.api.db.Database

class UserPostgresDataHandler @Inject() (
    protected val database: Database,
    userDAO: UserPostgresDAO)
    extends UserBlockingDataHandler
    with AnormPostgresDAL {

  override def create(email: UserEmail, password: UserHiddenPassword): ApplicationResult[User] = withConnection { implicit conn =>
    val userMaybe = userDAO.create(email, password)
    userMaybe.foreach { user =>
      val preferences = UserPreferences.default(user.id)
      val _ = userDAO.createUserPreferences(preferences)
    }
    
    Or.from(userMaybe, One(EmailAlreadyExists))
  }

  override def createVerificationToken(userId: UserId): ApplicationResult[UserVerificationToken] = withConnection { implicit conn =>
    val tokenMaybe = userDAO.createVerificationToken(userId)
    Or.from(tokenMaybe, One(UserVerificationTokenAlreadyExists))
  }

  override def verifyEmail(token: UserVerificationToken): ApplicationResult[User] = withConnection { implicit conn =>
    val userMaybe = userDAO.verifyEmail(token)
    Or.from(userMaybe, One(UserVerificationTokenNotFound))
  }

  override def getVerifiedUserPassword(email: UserEmail): ApplicationResult[UserHiddenPassword] = withConnection { implicit conn =>
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
}
