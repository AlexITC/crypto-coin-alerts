package com.alexitc.coinalerts.models

import com.alexitc.coinalerts.core.RandomIdGenerator
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json._

case class User(id: UserId, email: UserEmail)
object User {
  implicit val writes: Writes[User] = Json.writes[User]
}

case class UserId(string: String) extends AnyVal
object UserId {

  def create: UserId = UserId(RandomIdGenerator.stringId)

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

  /**
   * This method should be used only to wrap a password retrieved from
   * the database, otherwise use [[fromPassword]] method.
   */
  def fromDatabase(string: String): UserHiddenPassword = {
    new UserHiddenPassword(string)
  }
}

case class CreateUserModel(
    email: UserEmail,
    password: UserPassword,
    reCaptchaResponse: ReCaptchaResponse)
object CreateUserModel {
  implicit val reads: Reads[CreateUserModel] = Json.reads[CreateUserModel]
}

case class LoginByEmailModel(email: UserEmail, password: UserPassword)
object LoginByEmailModel {
  implicit val reads: Reads[LoginByEmailModel] = Json.reads[LoginByEmailModel]
}
