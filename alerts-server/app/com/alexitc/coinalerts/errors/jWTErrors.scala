package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.commons.AuthenticationError

sealed trait JWTError
case object AuthorizationHeaderRequiredError extends JWTError with AuthenticationError
case object InvalidJWTError extends JWTError with AuthenticationError
