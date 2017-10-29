package com.alexitc.coinalerts.commons

import org.scalactic.{Bad, Good}

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
  }
}
