package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.models.Exchange
import com.alexitc.coinalerts.services.NewCurrencyAlertService

class NewCurrencyAlertsController @Inject()(components: MyJsonControllerComponents, service: NewCurrencyAlertService)
    extends MyJsonController(components) {

  def create(exchange: Exchange) = authenticatedNoInput(Created) { context =>
    service.create(context.auth, exchange)
  }

  def get() = authenticatedNoInput { context =>
    service.get(context.auth)
  }

  def delete(exchange: Exchange) = authenticatedNoInput { context =>
    service.delete(context.auth, exchange)
  }
}
