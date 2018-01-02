package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{JsonController, JsonControllerComponents}
import com.alexitc.coinalerts.models.{Exchange, ExchangeCurrencyId, Market}
import com.alexitc.coinalerts.services.ExchangeCurrencyService
import com.alexitc.play.tracer.PlayRequestTracing

class ExchangeCurrenciesController @Inject() (
    components: JsonControllerComponents,
    exchangeCurrencyService: ExchangeCurrencyService)
    extends JsonController(components)
    with PlayRequestTracing {

  def getCurrency(exchangeCurrencyId: ExchangeCurrencyId) = publicNoInput { _ =>
    exchangeCurrencyService.getCurrency(exchangeCurrencyId)
  }

  def getCurrencies(exchange: Exchange, market: Market) = publicNoInput { _ =>
    exchangeCurrencyService.getCurrencies(exchange, market)
  }

  def getMarkets(exchange: Exchange) = publicNoInput { _ =>
    exchangeCurrencyService.getMarkets(exchange)
  }
}
