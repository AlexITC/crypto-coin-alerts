package com.alexitc.coinalerts.errors

/**
 * A PublicError could be displayed to anyone.
 */
sealed trait PublicError
case class FieldValidationError(field: String, message: String) extends PublicError
