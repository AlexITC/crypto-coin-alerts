package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{JsonController, JsonControllerComponents}
import com.alexitc.coinalerts.models.CreateDailyPriceAlertModel
import com.alexitc.coinalerts.services.DailyPriceAlertService
import com.alexitc.play.tracer.PlayRequestTracing

class DailyPriceAlertsController @Inject() (
    dailyPriceAlertService: DailyPriceAlertService,
    components: JsonControllerComponents)
    extends JsonController(components)
        with PlayRequestTracing {

  def create() = authenticatedWithInput { context: AuthCtxModel[CreateDailyPriceAlertModel] =>
    dailyPriceAlertService.create(context.userId, context.model)
  }
}
