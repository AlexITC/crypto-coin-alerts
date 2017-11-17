package com.alexitc.coinalerts.data.anorm.dao

import java.sql.Connection

import anorm.SQL
import com.alexitc.coinalerts.data.anorm.AnormParsers._
import com.alexitc.coinalerts.models._

class UserPostgresDAO {

  def create(email: UserEmail, password: UserHiddenPassword)(implicit conn: Connection): Option[User] = {
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

    userMaybe
  }

  def createVerificationToken(userId: UserId)(implicit conn: Connection): Option[UserVerificationToken] = {
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

    tokenMaybe
  }

  def verifyEmail(token: UserVerificationToken)(implicit conn: Connection): Option[User] = {
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

    userMaybe
  }

  def getVerifiedUserPassword(email: UserEmail)(implicit conn: Connection): Option[UserHiddenPassword] = {
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

    passwordMaybe
  }

  def getVerifiedUserByEmail(email: UserEmail)(implicit conn: Connection): Option[User] = {
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

    userMaybe
  }

  def getVerifiedUserById(userId: UserId)(implicit conn: Connection): Option[User] = {
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

    userMaybe
  }

  def createUserPreferences(userPreferences: UserPreferences)(implicit conn: Connection): Int = {
    SQL(
      """
        |INSERT INTO user_preferences
        |  (user_id, lang)
        |VALUES
        |  ({user_id}, {lang})
        |ON CONFLICT DO NOTHING
      """.stripMargin
    ).on(
      "user_id" -> userPreferences.userId.string,
      "lang" -> userPreferences.lang.code
    ).executeUpdate()
  }

  def getUserPreferences(userId: UserId)(implicit conn: Connection): Option[UserPreferences] = {
    SQL(
      """
        |SELECT user_id, lang
        |FROM user_preferences
        |WHERE user_id = {user_id}
      """.stripMargin
    ).on(
      "user_id" -> userId.string
    ).as(parseUserPreferences.singleOpt)
  }
}
