package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.core.{FilterQuery, Limit, Offset, PaginatedQuery}
import com.alexitc.coinalerts.models.{Exchange, ExchangeCurrencyId, Market, UserVerificationToken}
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

  implicit def exchangePathBinder(implicit binder: PathBindable[String]) = new PathBindable[Exchange] {
    override def bind(key: String, value: String): Either[String, Exchange] = {
      val result = for {
        string <- binder.bind(key, value).right
      } yield Exchange.fromString(string)

      result.right.flatMap {
        case Some(exchange) => Right(exchange)
        case None => Left("error.exchange.unknown")
      }
    }

    override def unbind(key: String, value: Exchange): String = {
      value.string
    }
  }

  implicit def marketPathBinder(implicit binder: PathBindable[String]) = new PathBindable[Market] {
    override def bind(key: String, value: String): Either[String, Market] = {
      for {
        string <- binder.bind(key, value).right
      } yield Market(string)
    }

    override def unbind(key: String, value: Market): String = {
      value.string
    }
  }

  implicit def exchangeCurrencyIdPathBinder(implicit binder: PathBindable[Int]) = new PathBindable[ExchangeCurrencyId] {
    override def bind(key: String, value: String): Either[String, ExchangeCurrencyId] = {
      for {
        int <- binder.bind(key, value).right
      } yield ExchangeCurrencyId(int)
    }

    override def unbind(key: String, value: ExchangeCurrencyId): String = {
      binder.unbind(key, value.int)
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

  implicit def filterQueryBinder(implicit binder: QueryStringBindable[String]) = new QueryStringBindable[FilterQuery] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, FilterQuery]] = {
      val result = for {
        // the filter query is optional
        string <- binder.bind("filter", params).getOrElse(Right(""))
      } yield FilterQuery(string)

      Some(result)
    }

    override def unbind(key: String, value: FilterQuery): String = {
      binder.unbind("filter", value.string)
    }
  }
}
