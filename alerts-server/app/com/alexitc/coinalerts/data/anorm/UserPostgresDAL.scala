package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import anorm._
import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.data.UserDAL
import com.alexitc.coinalerts.data.anorm.AnormParsers._
import com.alexitc.coinalerts.errors.EmailAlreadyExists
import com.alexitc.coinalerts.models.{User, UserEmail, UserHiddenPassword, UserId}
import org.scalactic.{One, Or}
import play.api.db.Database

// TODO: exception handling
class UserPostgresDAL @Inject() (database: Database) extends UserDAL {

  def create(email: UserEmail, password: UserHiddenPassword): ApplicationResult[User] = database.withConnection { implicit conn =>
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
}
