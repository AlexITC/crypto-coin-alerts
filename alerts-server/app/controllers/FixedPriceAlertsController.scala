package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{JsonController, JsonControllerComponents}
import com.alexitc.coinalerts.core.{FilterQuery, PaginatedQuery}
import com.alexitc.coinalerts.models.{CreateFixedPriceAlertModel, FixedPriceAlertId}
import com.alexitc.coinalerts.services.FixedPriceAlertService

class FixedPriceAlertsController @Inject() (
    components: JsonControllerComponents,
    alertService: FixedPriceAlertService)
    extends JsonController(components) {

  def create() = authenticatedWithInput(Created) { context: AuthCtxModel[CreateFixedPriceAlertModel] =>
    alertService.create(context.model, context.userId)
  }

  def getAlerts(query: PaginatedQuery, filterQuery: FilterQuery) = authenticatedNoInput { context: AuthCtx =>
    alertService.getAlerts(context.userId, query, filterQuery)
  }

  def delete(id: FixedPriceAlertId) = authenticatedNoInput { context: AuthCtx =>
    alertService.delete(id, context.userId)
  }
}
