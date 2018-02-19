package com.alexitc.coinalerts

import org.scalactic.{Every, Or}

import scala.concurrent.Future

package object commons {

  type ApplicationErrors = Every[ApplicationError]
  type ApplicationResult[+A] = A Or ApplicationErrors
  type FutureApplicationResult[+A] = Future[ApplicationResult[A]]
}
