package com.alexitc.coinalerts.models

import java.util.UUID

import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.{JsPath, JsString, Reads, Writes}

case class User(id: UserId, email: UserEmail)

case class UserId(string: String) extends AnyVal
object UserId {

  def create: UserId = UserId(UUID.randomUUID().toString.replace("-", ""))

  implicit val writes: Writes[UserId] = Writes[UserId] { userId => JsString(userId.string) }
}

case class UserEmail(string: String) extends AnyVal
object UserEmail {
  implicit val reads: Reads[UserEmail] = JsPath.read[String].map(UserEmail.apply)
  implicit val writes: Writes[UserEmail] = Writes[UserEmail] { userEmail => JsString(userEmail.string) }
}

case class UserPassword(string: String) extends AnyVal
object UserPassword {
  implicit val reads: Reads[UserPassword] = JsPath.read[String].map(UserPassword.apply)
  implicit val writes: Writes[UserPassword] = Writes[UserPassword] { userPassword => JsString(userPassword.string) }
}

class UserHiddenPassword private (val string: String) extends AnyVal
object UserHiddenPassword {
  def fromPassword(userPassword: UserPassword): UserHiddenPassword = {
    val string = BCrypt.hashpw(userPassword.string, BCrypt.gensalt())
    new UserHiddenPassword(string)
  }
}
