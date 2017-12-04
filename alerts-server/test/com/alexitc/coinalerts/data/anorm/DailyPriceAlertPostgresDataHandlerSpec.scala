package com.alexitc.coinalerts.data.anorm

import com.alexitc.coinalerts.commons.{DataHelper, PostgresDataHandlerSpec}
import com.alexitc.coinalerts.data.anorm.dao.{DailyPriceAlertDAO, UserPostgresDAO}
import com.alexitc.coinalerts.errors.RepeatedDailyPriceAlertError
import com.alexitc.coinalerts.models.{Book, CreateDailyPriceAlertModel, Market}
import org.scalactic.Bad

class DailyPriceAlertPostgresDataHandlerSpec extends PostgresDataHandlerSpec {

  lazy val dailyPriceAlertDataHandler = new DailyPriceAlertPostgresDataHandler(database, new DailyPriceAlertDAO)
  implicit lazy val userDataHandler = new UserPostgresDataHandler(database, new UserPostgresDAO)

  "Creating a daily price alert" should {
    "be able to create a valid alert" in {
      val user = DataHelper.createVerifiedUser()
      val model = CreateDailyPriceAlertModel(Market.BITTREX, Book.fromString("BTC_ETH").get)
      val result = dailyPriceAlertDataHandler.create(user.id, model).get

      result.book mustEqual model.book
      result.market mustEqual model.market
      result.userId mustEqual user.id
    }

    "reject a repeated alert" in {
      val user = DataHelper.createVerifiedUser()
      val model = CreateDailyPriceAlertModel(Market.BITTREX, Book.fromString("BTC_ETH").get)
      dailyPriceAlertDataHandler.create(user.id, model)
      val result = dailyPriceAlertDataHandler.create(user.id, model)

      result mustEqual Bad(RepeatedDailyPriceAlertError).accumulating
    }
  }
}
