package com.alexitc.coinalerts.services

import com.alexitc.coinalerts.commons.ExecutionContexts._
import com.alexitc.coinalerts.commons.RandomDataGenerator
import com.alexitc.coinalerts.config.FixedPriceAlertConfig
import com.alexitc.coinalerts.data.async.FixedPriceAlertFutureDataHandler
import com.alexitc.coinalerts.data.{FixedPriceAlertBlockingDataHandler, FixedPriceAlertInMemoryDataHandler}
import com.alexitc.coinalerts.errors.TooManyFixedPriceAlertsError
import com.alexitc.coinalerts.models.UserId
import com.alexitc.coinalerts.parsers.{FixedPriceAlertFilterParser, FixedPriceAlertOrderByParser}
import com.alexitc.coinalerts.services.validators.FixedPriceAlertValidator
import com.alexitc.playsonify.models.Count
import com.alexitc.playsonify.validators.PaginatedQueryValidator
import org.scalactic.Bad
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}

class FixedPriceAlertServiceSpec extends WordSpec with MustMatchers with ScalaFutures {

  "creating an alert" should {
    val model = RandomDataGenerator.createFixedPriceAlertModel(RandomDataGenerator.exchangeCurrencyId)

    "allow to create alert when " in {
      val service = fixedPriceAlertService(1)
      val userId = UserId.create

      whenReady(service.create(model, userId)) { result =>
        result.isGood mustEqual true
      }
    }

    "count only non-triggered alerts for a user" in {
      val datahandler = defaultBlockingDataHandler
      val service = fixedPriceAlertService(1, datahandler)

      val userId = UserId.create
      val triggeredAlert = datahandler.create(model, userId).get
      datahandler.markAsTriggered(triggeredAlert.id)

      whenReady(service.create(model, userId)) { result =>
        result.isGood mustEqual true
      }
    }

    "restrict the maximum number of non-triggered alerts for a user" in {
      val datahandler = defaultBlockingDataHandler
      val service = fixedPriceAlertService(1, datahandler)

      val userId = UserId.create
      datahandler.create(model, userId).get

      whenReady(service.create(model, userId)) { result =>
        result mustEqual Bad(TooManyFixedPriceAlertsError(Count(1))).accumulating
      }
    }

    "restrict the maximum number of alerts for a user when it has passed the limit" in {
      val datahandler = defaultBlockingDataHandler
      val service = fixedPriceAlertService(1, datahandler)

      val userId = UserId.create
      datahandler.create(model, userId)
      datahandler.create(model, userId)

      whenReady(service.create(model, userId)) { result =>
        result mustEqual Bad(TooManyFixedPriceAlertsError(Count(1))).accumulating
      }
    }
  }

  private def defaultBlockingDataHandler: FixedPriceAlertBlockingDataHandler = {
    new FixedPriceAlertInMemoryDataHandler {}
  }

  private def fixedPriceAlertService(
      maxNumberOfAlerts: Int,
      dataHandler: FixedPriceAlertBlockingDataHandler = defaultBlockingDataHandler): FixedPriceAlertService = {

    val validator = new FixedPriceAlertValidator
    val paginatedQueryValidator = new PaginatedQueryValidator

    val config = new FixedPriceAlertConfig {
      override def maximumNumberOfAlertsPerUser: Count = Count(maxNumberOfAlerts)
    }

    val futureDataHandler = new FixedPriceAlertFutureDataHandler(dataHandler)
    new FixedPriceAlertService(
      validator,
      paginatedQueryValidator,
      config,
      new FixedPriceAlertFilterParser,
      new FixedPriceAlertOrderByParser,
      futureDataHandler)(globalEC)
  }
}
