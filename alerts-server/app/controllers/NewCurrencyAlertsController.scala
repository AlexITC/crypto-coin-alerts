package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{JsonController, JsonControllerComponents}
import com.alexitc.coinalerts.models.{CreateNewCurrencyAlertModel, NewCurrencyAlertId}
import com.alexitc.coinalerts.services.NewCurrencyAlertService

class NewCurrencyAlertsController @Inject() (
    components: JsonControllerComponents,
    service: NewCurrencyAlertService)
    extends JsonController(components) {

  def create() = authenticatedWithInput(Created) { context: AuthCtxModel[CreateNewCurrencyAlertModel] =>
    service.create(context.userId, context.model.exchange)
  }

  def get() = authenticatedNoInput { context =>
    service.get(context.userId)
  }

  def delete(id: NewCurrencyAlertId) = authenticatedNoInput { context =>
    service.delete(id, context.userId)
  }
}
