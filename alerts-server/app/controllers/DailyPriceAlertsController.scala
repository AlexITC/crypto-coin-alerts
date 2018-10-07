package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.models.CreateDailyPriceAlertModel
import com.alexitc.coinalerts.services.DailyPriceAlertService
import com.alexitc.playsonify.models.PaginatedQuery

class DailyPriceAlertsController @Inject()(
    dailyPriceAlertService: DailyPriceAlertService,
    components: MyJsonControllerComponents)
    extends MyJsonController(components) {

  def create() = authenticatedWithInput { context: AuthCtxModel[CreateDailyPriceAlertModel] =>
    dailyPriceAlertService.create(context.auth, context.model)
  }

  def getAlerts(query: PaginatedQuery) = authenticatedNoInput { context: AuthCtx =>
    dailyPriceAlertService.getAlerts(context.auth, query)
  }
}
