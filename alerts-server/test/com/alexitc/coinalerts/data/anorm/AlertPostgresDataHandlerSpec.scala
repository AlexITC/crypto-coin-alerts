package com.alexitc.coinalerts.data.anorm

import com.alexitc.coinalerts.common.DataHelper._
import com.alexitc.coinalerts.common.{PostgresDALSpec, RandomDataGenerator}
import com.alexitc.coinalerts.data.anorm.dao.{AlertPostgresDAO, UserPostgresDAO}
import com.alexitc.coinalerts.errors.{AlertNotFound, InvalidPriceError}
import com.alexitc.coinalerts.models._
import org.scalactic.Bad

class AlertPostgresDataHandlerSpec extends PostgresDALSpec {

  implicit lazy val userPostgresDataHandler = new UserPostgresDataHandler(database, new UserPostgresDAO)
  lazy val alertPostgresDataHandler = new AlertPostgresDataHandler(database, new AlertPostgresDAO)
  lazy val verifiedUser = createVerifiedUser()

  val createDefaultAlertModel = CreateAlertModel(AlertType.DEFAULT, Market.BITSO, Book.fromString("BTC_MXN").get, true, BigDecimal("5000.00"), None)
  val createBasePriceAlertModel = createDefaultAlertModel.copy(alertType = AlertType.BASE_PRICE, basePrice = Some(BigDecimal("4000.00")))

  "Creating an alert" should {

    "ba able to create a DEFAULT alert" in {
      val result = alertPostgresDataHandler.create(createDefaultAlertModel, verifiedUser.id)
      result.isGood mustEqual true
    }

    "be able to create a BASE_PRICE alert" in {
      val result = alertPostgresDataHandler.create(createBasePriceAlertModel, verifiedUser.id)
      result.isGood mustEqual true
    }

    "fail to create an alert for a non existent user" in {
      val result = alertPostgresDataHandler.create(createDefaultAlertModel, UserId.create)
      result.isBad mustEqual true
    }

    "fail to create a BASE_PRICE alert without basePrice" in {
      val result = alertPostgresDataHandler.create(createDefaultAlertModel.copy(alertType = AlertType.BASE_PRICE), UserId.create)
      result.isBad mustEqual true
    }
  }

  "markAsTriggered" should {
    "mark an existing alert as triggered" in {
      val user = createUnverifiedUser()
      val alert = alertPostgresDataHandler.create(RandomDataGenerator.createDefaultAlertModel(), user.id).get
      val result = alertPostgresDataHandler.markAsTriggered(alert.id)
      result.isGood mustEqual true
    }

    "fail to mark an already triggered alert" in {
      val user = createUnverifiedUser()
      val alert = alertPostgresDataHandler.create(RandomDataGenerator.createDefaultAlertModel(), user.id).get
      alertPostgresDataHandler.markAsTriggered(alert.id)

      val result = alertPostgresDataHandler.markAsTriggered(alert.id)
      result mustEqual Bad(AlertNotFound).accumulating
    }

    "fail to mark a non existent alert" in {
      val result = alertPostgresDataHandler.markAsTriggered(RandomDataGenerator.alertId)
      result mustEqual Bad(AlertNotFound).accumulating
    }
  }

  "findAlertsByPrice" should {

    "retrieve an alert requiring the current price to be greater than the given price" in {
      val user = createUnverifiedUser()
      val market = Market.BITSO
      val book = Book("BTC", "MXN")
      val givenPrice = BigDecimal("1000")
      val createModel = RandomDataGenerator.createDefaultAlertModel(market = market, book = book, givenPrice = givenPrice, isGreaterThan = true)
      val alert = alertPostgresDataHandler.create(createModel, user.id).get
      val currentPrice = BigDecimal("1000.00000001")
      val result = alertPostgresDataHandler.findPendingAlertsForPrice(market, book, currentPrice).get
      result.exists(_.id == alert.id) mustEqual true
    }

    "retrieve an alert requiring the current price to be lower than the given price" in {
      val user = createUnverifiedUser()
      val market = Market.BITSO
      val book = Book("BTC", "MXN")
      val givenPrice = BigDecimal("1000")
      val createModel = RandomDataGenerator.createDefaultAlertModel(market = market, book = book, givenPrice = givenPrice, isGreaterThan = false)
      val alert = alertPostgresDataHandler.create(createModel, user.id).get
      val currentPrice = BigDecimal("999.99999999")
      val result = alertPostgresDataHandler.findPendingAlertsForPrice(market, book, currentPrice).get
      result.exists(_.id == alert.id) mustEqual true
    }

    "retrieve several pending alerts" in {
      val createAlertModel = CreateAlertModel(AlertType.DEFAULT, Market.BITSO, Book.fromString("BTC_MXN").get, true, BigDecimal("5000.00"), None)
      val createAlert1 = createAlertModel
      val createAlert2 = createAlertModel.copy(isGreaterThan = false)
      val createAlert3 = createAlertModel.copy(market = Market.BITTREX)
      val createAlert4 = createAlertModel.copy(book = Book("BTC", "ETH"))

      val user = createUnverifiedUser()
      val alert1 = alertPostgresDataHandler.create(createAlert1, user.id).get
      val alert2 = alertPostgresDataHandler.create(createAlert2, user.id).get
      val alert3 = alertPostgresDataHandler.create(createAlert3, user.id).get
      val alert4 = alertPostgresDataHandler.create(createAlert4, user.id).get

      val result1 = alertPostgresDataHandler.findPendingAlertsForPrice(Market.BITSO, Book("BTC", "MXN"), BigDecimal("5000.00000001")).get
      result1.exists(_.id == alert1.id) mustEqual true
      result1.exists(_.id == alert2.id) mustEqual false
      result1.exists(_.id == alert3.id) mustEqual false
      result1.exists(_.id == alert4.id) mustEqual false

      val result2 = alertPostgresDataHandler.findPendingAlertsForPrice(Market.BITSO, Book("BTC", "MXN"), BigDecimal("4999.99999999")).get
      result2.exists(_.id == alert1.id) mustEqual false
      result2.exists(_.id == alert2.id) mustEqual true
      result2.exists(_.id == alert3.id) mustEqual false
      result2.exists(_.id == alert4.id) mustEqual false
    }

    "not retrieve an alert that is already triggered" in {
      val user = createUnverifiedUser()
      val market = Market.BITSO
      val book = Book("BTC", "MXN")
      val givenPrice = BigDecimal("1000")
      val createModel = RandomDataGenerator.createDefaultAlertModel(market = market, book = book, givenPrice = givenPrice)
      val alert = alertPostgresDataHandler.create(createModel, user.id).get
      alertPostgresDataHandler.markAsTriggered(alert.id)

      val result = alertPostgresDataHandler.findPendingAlertsForPrice(market, book, givenPrice).get
      result.exists(_.id == alert.id) mustEqual false
    }

    "fail to filter by negative price" in {
      val currentPrice = BigDecimal("0")
      val result = alertPostgresDataHandler.findPendingAlertsForPrice(Market.BITSO, Book("BTC", "MXN"), currentPrice)
      result mustEqual Bad(InvalidPriceError).accumulating
    }
  }

  "retrieving a base price alert" should {
    "succeed when the alert exists" in {
      val user = createUnverifiedUser()
      val alert = alertPostgresDataHandler.create(createBasePriceAlertModel, user.id).get
      val basePriceAlert = alertPostgresDataHandler.findBasePriceAlert(alert.id).get
      basePriceAlert.basePrice mustEqual alert.basePrice.get
    }

    "fail when the alert doesn't exists" in {
      val user = createUnverifiedUser()
      val alert = alertPostgresDataHandler.create(createDefaultAlertModel, user.id).get
      val result = alertPostgresDataHandler.findBasePriceAlert(alert.id)
      result mustEqual Bad(AlertNotFound).accumulating
    }
  }
}
