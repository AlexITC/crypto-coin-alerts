package com.alexitc.coinalerts.commons

import play.api.libs.json.JsValue
import play.api.mvc.Request

/**
 * Allow to authenticate a request and map it to a value with type [[T]].
 *
 * A dummy service that gets the user id from the "Authorization" header or uses -1
 * when the header is not present could look like this, NEVER USE THIS PIECE OF CODE:
 * {{{
 *   class DummyAuthenticatorService extends AbstractAuthenticatorService {
 *     override def authenticate[A](request: Request[A]): FutureApplicationResult[Int] = {
 *       val userId = request.headers.get(HeaderNames.AUTHORIZATION).map(_.toInt).getOrElse(-1)
 *       Future.successful { Good(userId) }
 *     }
 *   }
 * }}}
 *
 * @tparam A the type representing what is useful to use in your controllers as the user or credentials.
 */
trait AbstractAuthenticatorService[A] {

  def authenticate(request: Request[JsValue]): FutureApplicationResult[A]

}
