package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.models.UserVerificationToken
import play.api.mvc.PathBindable

object PlayBinders {

  implicit def userVerificationTokenPathBinder(implicit binder: PathBindable[String]) = new PathBindable[UserVerificationToken] {
    override def bind(key: String, value: String): Either[String, UserVerificationToken] = {
      for {
        string <- binder.bind(key, value).right
      } yield UserVerificationToken(string)
    }

    override def unbind(key: String, token: UserVerificationToken): String = {
      token.string
    }
  }
}
