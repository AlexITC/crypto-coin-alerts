package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.core.{FilterQuery, OrderByQuery}
import com.alexitc.coinalerts.models.{CreateFixedPriceAlertModel, FixedPriceAlertId}
import com.alexitc.coinalerts.services.FixedPriceAlertService
import com.alexitc.playsonify.models.PaginatedQuery

class FixedPriceAlertsController @Inject() (
    components: MyJsonControllerComponents,
    alertService: FixedPriceAlertService)
    extends MyJsonController(components) {

  def create() = authenticatedWithInput(Created) { context: AuthCtxModel[CreateFixedPriceAlertModel] =>
    alertService.create(context.model, context.auth)
  }

  def getAlerts(
      query: PaginatedQuery,
      filterQuery: FilterQuery,
      orderByQuery: OrderByQuery) = authenticatedNoInput { context: AuthCtx =>

    alertService.getAlerts(context.auth, query, filterQuery, orderByQuery)
  }

  def delete(id: FixedPriceAlertId) = authenticatedNoInput { context: AuthCtx =>
    alertService.delete(id, context.auth)
  }
}
