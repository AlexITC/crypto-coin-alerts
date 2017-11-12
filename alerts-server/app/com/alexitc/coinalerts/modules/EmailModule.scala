package com.alexitc.coinalerts.modules

import com.alexitc.coinalerts.services.EmailServiceTrait
import com.alexitc.coinalerts.services.external.MailgunEmailService
import com.google.inject.AbstractModule

class EmailModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[EmailServiceTrait]).to(classOf[MailgunEmailService])
  }
}
