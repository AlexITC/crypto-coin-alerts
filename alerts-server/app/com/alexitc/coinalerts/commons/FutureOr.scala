package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.errors.ApplicationError
import org.scalactic.{Bad, Good, One, Or}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Monad transformer for composing values with [[FutureApplicationResult]] type.
 *
 * @param future
 * @tparam A
 */
class FutureOr[+A](val future: FutureApplicationResult[A]) extends AnyVal {

  def toFuture: Future[ApplicationResult[A]] = future

  def flatMap[B](f: A => FutureOr[B])(implicit ec: ExecutionContext): FutureOr[B] = {
    val newFuture = future.flatMap {
      case Good(a) => f(a).toFuture
      case Bad(error) => Future.successful(Bad(error))
    }

    new FutureOr(newFuture)
  }

  def map[B](f: A => B)(implicit ec: ExecutionContext): FutureOr[B] = {
    val newFuture = future.map { _.map(f) }
    new FutureOr(newFuture)
  }

  def mapWithError[B](f: A => B Or ApplicationErrors)(implicit ec: ExecutionContext): FutureOr[B] = {
    val newFuture = future.map {
      case Good(x) => f(x)
      case Bad(e) => Bad(e)
    }

    new FutureOr(newFuture)
  }
}

object FutureOr {
  object Implicits {
    implicit class FutureOps[+A](val future: FutureApplicationResult[A]) extends AnyVal {
      def toFutureOr: FutureOr[A] = {
        new FutureOr(future)
      }
    }

    implicit class OrOps[+A](val or: ApplicationResult[A]) extends AnyVal {
      def toFutureOr: FutureOr[A] = {
        val future = Future.successful(or)
        new FutureOr(future)
      }
    }

    implicit class OptionOps[+A](val option: Option[A]) extends AnyVal {
      def toFutureOr(error: ApplicationError): FutureOr[A] = {
        val or = Or.from(option, One(error))
        val future = Future.successful(or)
        new FutureOr(future)
      }
    }
  }
}
