package com.github.lukaszbudnik.vaultkeeper.v1.rest

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.{ContentTypes, StatusCodes}
import spray.testkit.Specs2RouteTest

@RunWith(classOf[JUnitRunner])
class StatusRoutesSpec extends Specification with Specs2RouteTest with JsonProtocol {

  val routes = new StatusRoutes {
    // return the Spec2RouteTest system
    override def statusActorSystem = system

    override def actorRefFactory = system
  }

  "The vaultkeeper service" should {

    "redirect to status for root path" in {
      Get() ~> routes.statusRoutes ~> check {
        handled === true
        status === StatusCodes.MovedPermanently
      }
    }

    "return JSON with status" in {
      Get("/status") ~> routes.statusRoutes ~> check {
        handled === true
        status === StatusCodes.OK
        body.contentType === ContentTypes.`application/json`
        responseAs[Status].startTime === system.startTime
        responseAs[Status].uptime must beGreaterThanOrEqualTo(0L)
      }
    }

  }

}
