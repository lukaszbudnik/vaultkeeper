package com.github.lukaszbudnik.vaultkeeper.rest

import spray.testkit.Specs2RouteTest
import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import spray.http.{StatusCodes, ContentTypes}
import org.specs2.runner.JUnitRunner

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
