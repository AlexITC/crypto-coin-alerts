package com.alexitc.coinalerts.services.external

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.config.MailgunConfig
import com.alexitc.coinalerts.errors.MailgunSendEmailError
import com.alexitc.coinalerts.models.{UserEmail, UserVerificationToken}
import com.alexitc.coinalerts.services.EmailServiceTrait
import com.alexitc.play.tracer.PlayRequestTracing
import org.scalactic.{Bad, Good}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.ws.{WSAuthScheme, WSClient}

import scala.concurrent.ExecutionContext

class MailgunEmailService @Inject() (
    ws: WSClient,
    config: MailgunConfig)(
    implicit ec: ExecutionContext)
    extends EmailServiceTrait
        with PlayRequestTracing {

  // TODO: Support i18n, request scoped lang could be one option
  def sendVerificationToken(email: UserEmail, token: UserVerificationToken): FutureApplicationResult[Unit] = {
    val result = ws
        .url(s"https://api.mailgun.net/v3/${config.domain.string}/messages")
        .withAuth("api", config.apiSecretKey.string, WSAuthScheme.BASIC)
        .addQueryStringParameters(
          "from" -> config.from.string,
          "to" -> email.string,
          "subject" -> "You have a new alert!",
          "text" -> "Here is the alert"
        )
        .get()
        .map { response =>
          logger.info(s"Mailgun response, status = ${response.status}, body = ${response.body}")
          Option(response)
              .filter(_.status == 200)
              .map(_.json)
              .flatMap { json =>
                json.validate[MailgunSendEmailResponse]
                    .map(Some(_))
                    .getOrElse(None)
              }
              .map { mailgunResponse =>
                logger.info(s"Mailgun response for email = [${email.string}], id = [${mailgunResponse.id}], message = [${mailgunResponse.message}]")
                Good(())
              }
              .getOrElse {
                logger.warn(s"Unexpected Mailgun response, status = ${response.status}, body = [${response.body}]")
                Bad(MailgunSendEmailError).accumulating
              }
        }

    result
  }
}

case class MailgunSendEmailResponse(id: String, message: String)
object MailgunSendEmailResponse {
  implicit val reads: Reads[MailgunSendEmailResponse] = {
    val builder = (JsPath \ "id").read[String] and
        (JsPath \ "message").read[String]

    builder( (id, message) => MailgunSendEmailResponse.apply(id, message) )
  }
}
