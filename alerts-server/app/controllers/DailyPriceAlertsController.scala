package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{JsonController, JsonControllerComponents}
import com.alexitc.coinalerts.core.PaginatedQuery
import com.alexitc.coinalerts.models.CreateDailyPriceAlertModel
import com.alexitc.coinalerts.services.DailyPriceAlertService

class DailyPriceAlertsController @Inject() (
    dailyPriceAlertService: DailyPriceAlertService,
    components: JsonControllerComponents)
    extends JsonController(components) {

  def create() = authenticatedWithInput { context: AuthCtxModel[CreateDailyPriceAlertModel] =>
    dailyPriceAlertService.create(context.userId, context.model)
  }

  def getAlerts(query: PaginatedQuery) = authenticatedNoInput { context: AuthCtx =>
    dailyPriceAlertService.getAlerts(context.userId, query)
  }
}
