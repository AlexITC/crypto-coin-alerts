package controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}

class HealthController @Inject() (components: ControllerComponents)
    extends AbstractController(components) {

  def check() = Action {
    Ok
  }
}
