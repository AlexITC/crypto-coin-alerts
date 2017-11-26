package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.core.{Limit, Offset, PaginatedQuery}
import com.alexitc.coinalerts.models.UserVerificationToken
import play.api.mvc.{PathBindable, QueryStringBindable}

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

  private val DefaultOffset = 0
  private val DefaultLimit = 20

  implicit def paginatedQueryBinder(implicit binder: QueryStringBindable[Int]) = new QueryStringBindable[PaginatedQuery] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PaginatedQuery]] = {
      val result = for {
        offsetInt <- binder.bind("offset", params).getOrElse(Right(DefaultOffset))
        limitInt <- binder.bind("limit", params).getOrElse(Right(DefaultLimit))
      } yield PaginatedQuery(Offset(offsetInt), Limit(limitInt))

      Some(result)
    }

    override def unbind(key: String, value: PaginatedQuery): String = {
      val offsetParam = binder.unbind("offset", value.offset.int)
      val limitParam = binder.unbind("limit", value.limit.int)

      List(offsetParam, limitParam).mkString("&")
    }
  }
}
