package com.alexitc.coinalerts.data.anorm

import anorm.SqlParser._
import anorm._
import com.alexitc.coinalerts.models._
import org.postgresql.util.PGobject

object AnormParsers {

  val parseUserId = str("user_id").map(UserId.apply)
  val parseEmail = str("email")(citextToString).map(UserEmail.apply)
  val parseUserVerificationToken = str("token").map(UserVerificationToken.apply)

  val parseUser = (parseUserId ~ parseEmail).map {
    case userId ~ email => User.apply(userId, email)
  }

  val parsePassword = str("password").map(UserHiddenPassword.fromDatabase)

  def citextToString: Column[String] = Column.nonNull { case (value, meta) =>
    val MetaDataItem(qualified, _, clazz) = meta
    value match {
      case str: String => Right(str)
      case obj: PGobject if "citext" equalsIgnoreCase obj.getType => Right(obj.getValue)
      case _ => Left(TypeDoesNotMatch(s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to String for column $qualified, class = $clazz"))
    }
  }
}
