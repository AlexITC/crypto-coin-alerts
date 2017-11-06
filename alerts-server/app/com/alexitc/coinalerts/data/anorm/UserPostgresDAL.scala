package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import anorm._
import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.data.UserDAL
import com.alexitc.coinalerts.data.anorm.AnormParsers._
import com.alexitc.coinalerts.errors._
import com.alexitc.coinalerts.models._
import org.scalactic.{One, Or}
import play.api.db.Database

class UserPostgresDAL @Inject() (protected val database: Database) extends UserDAL with AnormPostgresDAL {

  def create(email: UserEmail, password: UserHiddenPassword): ApplicationResult[User] = withConnection { implicit conn =>
    val userId = UserId.create

    val userMaybe = SQL(
      """
        |INSERT INTO users (user_id, email, password)
        |VALUES ({user_id}, {email}, {password})
        |ON CONFLICT (email) DO NOTHING
        |RETURNING user_id, email
      """.stripMargin
    ).on(
      "user_id" -> userId.string,
      "email" -> email.string,
      "password" -> password.string
    ).as(parseUser.singleOpt)

    Or.from(userMaybe, One(EmailAlreadyExists))
  }

  def createVerificationToken(userId: UserId): ApplicationResult[UserVerificationToken] = withConnection { implicit conn =>
    val token = UserVerificationToken.create(userId)

    val tokenMaybe = SQL(
      """
        |INSERT INTO user_verification_tokens
        |  (user_id, token)
        |VALUES
        |  ({user_id}, {token})
        |ON CONFLICT DO NOTHING
        |RETURNING token
      """.stripMargin
    ).on(
      "user_id" -> userId.string,
      "token" -> token.string
    ).as(parseUserVerificationToken.singleOpt)

    Or.from(tokenMaybe, One(UserVerificationTokenAlreadyExists))
  }

  def verifyEmail(token: UserVerificationToken): ApplicationResult[User] = withConnection { implicit conn =>
    val userMaybe = SQL(
      """
         |UPDATE users u
         |SET verified_on = NOW()
         |FROM user_verification_tokens t
         |WHERE u.user_id = t.user_id AND
         |      u.verified_on IS NULL AND
         |      token = {token}
         |RETURNING u.user_id, u.email
       """.stripMargin
    ).on(
      "token" -> token.string
    ).as(parseUser.singleOpt)

    Or.from(userMaybe, One(UserVerificationTokenNotFound))
  }

  override def getVerifiedUserPassword(email: UserEmail): ApplicationResult[UserHiddenPassword] = withConnection { implicit conn =>
    val passwordMaybe = SQL(
      """
        |SELECT password
        |FROM users
        |WHERE verified_on IS NOT NULL AND
        |      email = {email}
      """.stripMargin
    ).on(
      "email" -> email.string
    ).as(parsePassword.singleOpt)

    Or.from(passwordMaybe, One(VerifiedUserNotFound))
  }

  override def getVerifiedUserByEmail(email: UserEmail): ApplicationResult[User] = withConnection { implicit conn =>
    val userMaybe = SQL(
      """
        |SELECT user_id, email
        |FROM users
        |WHERE verified_on IS NOT NULL AND
        |      email = {email}
      """.stripMargin
    ).on(
      "email" -> email.string
    ).as(parseUser.singleOpt)

    Or.from(userMaybe, One(VerifiedUserNotFound))
  }

  override def getVerifiedUserById(userId: UserId): ApplicationResult[User] = withConnection { implicit conn =>
    val userMaybe = SQL(
      """
        |SELECT user_id, email
        |FROM users
        |WHERE verified_on IS NOT NULL AND
        |      user_id = {user_id}
      """.stripMargin
    ).on(
      "user_id" -> userId.string
    ).as(parseUser.singleOpt)

    Or.from(userMaybe, One(VerifiedUserNotFound))
  }
}
