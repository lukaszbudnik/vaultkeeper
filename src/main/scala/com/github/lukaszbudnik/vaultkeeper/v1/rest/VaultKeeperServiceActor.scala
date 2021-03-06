package com.github.lukaszbudnik.vaultkeeper.v1.rest

import javax.inject.Inject

import akka.actor.Actor
import akka.event.Logging
import com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt.MngmntAuthService
import spray.http.MediaTypes
import spray.httpx.Json4sJacksonSupport

class VaultKeeperServiceActor @Inject()(val mngmntUserStoreService: MngmntAuthService)
  extends Actor with VaultKeeperV1Routes with StatusRoutes with JsonProtocol {
  def actorRefFactory = context

  def receive = runRoute(
    logRequest("vaultkeeper", Logging.DebugLevel) {
      logResponse("vaultkeeper", Logging.DebugLevel) {
        respondWithMediaType(MediaTypes.`application/json`) {
          statusRoutes ~ vaultKeeperV1Routes
        }
      }
    })
}

trait JsonProtocol extends Json4sJacksonSupport {
  implicit def json4sJacksonFormats = RestJsonProtocol.json4sJacksonFormats + StatusJsonProtocol.json4sJacksonFormats
}
