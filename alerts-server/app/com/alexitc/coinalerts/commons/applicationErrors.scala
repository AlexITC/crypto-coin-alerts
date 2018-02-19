package com.alexitc.coinalerts.commons

// Top-level errors
trait ApplicationError
trait InputValidationError extends ApplicationError
trait ConflictError extends ApplicationError
trait NotFoundError extends ApplicationError
trait AuthenticationError extends ApplicationError
trait ServerError extends ApplicationError {
  // contains data private to the server
  def cause: Throwable
}
