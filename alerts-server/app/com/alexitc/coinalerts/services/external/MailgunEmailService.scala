package com.alexitc.coinalerts.services.external

import javax.inject.Inject
import com.alexitc.coinalerts.config.{ExternalServiceExecutionContext, MailgunConfig}
import com.alexitc.coinalerts.errors.MailgunSendEmailError
import com.alexitc.coinalerts.models.UserEmail
import com.alexitc.coinalerts.services.EmailMessagesProvider.{EmailSubject, EmailText}
import com.alexitc.coinalerts.services.EmailServiceTrait
import com.alexitc.coinalerts.services.external.MailgunEmailService.SendEmailResponse
import com.alexitc.playsonify.core.FutureApplicationResult
import org.scalactic.{Bad, Good}
import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.ws.{WSAuthScheme, WSClient}
import play.utils.UriEncoding

class MailgunEmailService @Inject() (
    ws: WSClient,
    config: MailgunConfig)(
    implicit ec: ExternalServiceExecutionContext)
    extends EmailServiceTrait {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def sendEmail(
      destination: UserEmail,
      subject: EmailSubject,
      text: EmailText): FutureApplicationResult[Unit] = {

    val domainEncoded = UriEncoding.encodePathSegment(config.domain.string, "UTF-8")
    val url = s"https://api.mailgun.net/v3/$domainEncoded/messages"
    val result = ws
        .url(url)
        .withAuth("api", config.apiSecretKey.string, WSAuthScheme.BASIC)
        .addQueryStringParameters(
          "from" -> config.from.string,
          "to" -> destination.string,
          "subject" -> subject.string,
          "text" -> text.string
        )
        .post("")
        .map { response =>
          Option(response)
              .filter(_.status == 200)
              .map(_.json)
              .flatMap { json =>
                json.validate[SendEmailResponse]
                    .map(Some(_))
                    .getOrElse(None)
              }
              .map { mailgunResponse =>
                logger.info(s"Mailgun response for email = [${destination.string}], id = [${mailgunResponse.id}], message = [${mailgunResponse.message}]")
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

object MailgunEmailService{

  case class SendEmailResponse(id: String, message: String)

  object SendEmailResponse {
    implicit val reads: Reads[SendEmailResponse] = {
      val builder = (JsPath \ "id").read[String] and
        (JsPath \ "message").read[String]

      builder((id, message) => SendEmailResponse.apply(id, message))
    }
  }
}
