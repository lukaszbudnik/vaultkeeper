package com.github.lukaszbudnik.vaultkeeper.rest

import akka.actor.ActorContext
import org.json4s.FieldSerializer
import spray.http.{StatusCodes, Uri}
import spray.routing.HttpService

case class Status(uptime: Long, startTime: Long)

object StatusJsonProtocol {
  lazy val json4sJacksonFormats = FieldSerializer[Status]()
}

trait StatusRoutes extends HttpService with JsonProtocol {
  def statusActorSystem = actorRefFactory.asInstanceOf[ActorContext].system

  val statusRoutes =
    pathSingleSlash {
      redirect(Uri("status"), StatusCodes.MovedPermanently)
    } ~ {
      path("status" ~ Slash.?) {
        get {
          complete(Status(statusActorSystem.uptime, statusActorSystem.startTime))
        }
      }
    }
}

