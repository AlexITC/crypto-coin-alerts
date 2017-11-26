package com.alexitc.coinalerts.controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{JsonController, JsonControllerComponents}
import com.alexitc.coinalerts.core.PaginatedQuery
import com.alexitc.coinalerts.models.CreateFixedPriceAlertModel
import com.alexitc.coinalerts.services.FixedPriceAlertService
import com.alexitc.play.tracer.PlayRequestTracing

class FixedPriceAlertsController @Inject() (
    components: JsonControllerComponents,
    alertService: FixedPriceAlertService)
    extends JsonController(components)
    with PlayRequestTracing {

  def create() = authenticatedWithInput(Created) { context: AuthCtxModel[CreateFixedPriceAlertModel] =>
    alertService.create(context.model, context.userId)
  }

  def getAlerts(query: PaginatedQuery) = authenticatedNoInput { context: AuthCtx =>
    alertService.getAlerts(context.userId, query)
  }
}
