package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.models.{Exchange, ExchangeCurrencyId, Market}
import com.alexitc.coinalerts.services.ExchangeCurrencyService

class ExchangeCurrenciesController @Inject()(
    components: MyJsonControllerComponents,
    exchangeCurrencyService: ExchangeCurrencyService)
    extends MyJsonController(components) {

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
