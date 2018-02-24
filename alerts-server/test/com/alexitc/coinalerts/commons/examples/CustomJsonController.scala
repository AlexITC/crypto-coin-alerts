package com.alexitc.coinalerts.commons.examples

import com.alexitc.coinalerts.commons.{AbstractJsonController, ServerError}
import com.alexitc.coinalerts.core.ErrorId
import org.slf4j.LoggerFactory

class CustomJsonController (components: CustomControllerComponents) extends AbstractJsonController(components) {

  protected val logger = LoggerFactory.getLogger(this.getClass)

  override protected def onServerError(error: ServerError, errorId: ErrorId): Unit = {
    logger.error(s"Unexpected internal error = ${errorId.string}", error.cause)
  }
}
