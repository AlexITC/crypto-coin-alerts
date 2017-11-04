package com.alexitc.coinalerts.models

import java.util.UUID

import play.api.libs.json.{JsPath, Reads}

case class UserVerificationToken(string: String) extends AnyVal
object UserVerificationToken {

  def create(userId: UserId): UserVerificationToken = {
    val randomId = UUID.randomUUID().toString.replace("-", "")
    val token = s"${userId.string}.$randomId"
    UserVerificationToken(token)
  }
}

case class SendVerificationTokenModel(email: UserEmail)
object SendVerificationTokenModel {
  implicit val reads: Reads[SendVerificationTokenModel] = {
    (JsPath \ "email")
        .read[UserEmail]
        .map(SendVerificationTokenModel.apply)
  }
}
