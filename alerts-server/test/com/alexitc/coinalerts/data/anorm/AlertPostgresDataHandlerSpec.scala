package com.alexitc.coinalerts.data.anorm

import com.alexitc.coinalerts.common.DataHelper._
import com.alexitc.coinalerts.common.PostgresDALSpec
import com.alexitc.coinalerts.data.anorm.dao.AlertPostgresDAO
import com.alexitc.coinalerts.models._

class AlertPostgresDataHandlerSpec extends PostgresDALSpec {

  implicit lazy val userPostgresDAL = new UserPostgresDAL(database)
  lazy val alertPostgresDataHandler = new AlertPostgresDataHandler(database, new AlertPostgresDAO)
  lazy val verifiedUser = createVerifiedUser()

  "Creating an alert" should {
    val createDefaultAlertModel = CreateAlertModel(AlertType.DEFAULT, Market.BITSO, Book.fromString("BTC_MXN").get, true, BigDecimal("5000.00"), None)
    val createBasePriceAlertModel = createDefaultAlertModel.copy(alertType = AlertType.BASE_PRICE, basePrice = Some(BigDecimal("4000.00")))

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
}
