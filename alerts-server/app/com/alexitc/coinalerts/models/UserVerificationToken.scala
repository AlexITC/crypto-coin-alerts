package com.alexitc.coinalerts.models

import java.util.UUID

case class UserVerificationToken(string: String) extends AnyVal
object UserVerificationToken {

  def create(userId: UserId): UserVerificationToken = {
    val randomId = UUID.randomUUID().toString.replace("-", "")
    val token = s"${userId.string}.$randomId"
    UserVerificationToken(token)
  }
}
