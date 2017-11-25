package com.alexitc.coinalerts.controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{AuthenticatedRequestContextWithModel, JsonController, JsonControllerComponents}
import com.alexitc.coinalerts.models.CreateAlertModel
import com.alexitc.coinalerts.services.AlertService
import com.alexitc.play.tracer.PlayRequestTracing

class AlertsController @Inject() (
    components: JsonControllerComponents,
    alertService: AlertService)
    extends JsonController(components)
    with PlayRequestTracing {

  def create() = authenticatedWithInput(Created) { context: AuthenticatedRequestContextWithModel[CreateAlertModel] =>
    alertService.create(context.model, context.userId)
  }
}
