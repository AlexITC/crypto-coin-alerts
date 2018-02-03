package com.alexitc.coinalerts.data.anorm.parsers

import anorm.SqlParser.str
import anorm.~
import com.alexitc.coinalerts.models._
import play.api.i18n.Lang

object UserParsers {

  import CommonParsers._

  val parseUserId = str("user_id").map(UserId.apply)
  val parseEmail = str("email")(citextToString).map(UserEmail.apply)
  val parsePassword = str("password").map(UserHiddenPassword.fromDatabase)
  val parseUserVerificationToken = str("token").map(UserVerificationToken.apply)
  val parseLang = str("lang").map(Lang.apply)

  val parseUser = (parseUserId ~ parseEmail).map {
    case userId ~ email => User.apply(userId, email)
  }

  val parseUserPreferences = (parseUserId ~ parseLang).map {
    case userId ~ lang => UserPreferences(userId, lang)
  }
}
