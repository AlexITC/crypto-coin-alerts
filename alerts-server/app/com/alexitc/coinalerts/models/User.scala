package com.alexitc.coinalerts.models

import java.util.UUID

import com.alexitc.coinalerts.commons.ModelCreated
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.functional.syntax._
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

case class CreateUserModel(email: UserEmail, password: UserPassword)
object CreateUserModel {
  implicit val reads: Reads[CreateUserModel] = {
    val builder = (JsPath \ "email").read[UserEmail] and
        (JsPath \ "password").read[UserPassword]

    builder(CreateUserModel.apply _)
  }
}

case class UserCreatedModel(id: UserId, email: UserEmail) extends ModelCreated
object UserCreatedModel {
  implicit val writes: Writes[UserCreatedModel] = (
      (JsPath \ "id").write[UserId] and
          (JsPath \ "email").write[UserEmail]
      )(unlift(UserCreatedModel.unapply))
}
