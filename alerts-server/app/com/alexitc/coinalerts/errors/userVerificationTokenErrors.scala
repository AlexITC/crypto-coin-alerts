package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.commons.{ConflictError, NotFoundError}

// Verify user email
sealed trait UserVerificationTokenError
case object UserVerificationTokenNotFoundError extends UserVerificationTokenError with NotFoundError
case object UserVerificationTokenAlreadyExistsError extends UserVerificationTokenError with ConflictError
