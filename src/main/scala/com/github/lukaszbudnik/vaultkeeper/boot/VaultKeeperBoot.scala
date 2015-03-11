package com.github.lukaszbudnik.vaultkeeper

import akka.actor.ActorSystem
import akka.io.IO
import com.github.lukaszbudnik.vaultkeeper.core.{VaultKeeperActorProvider, VaultKeeperModule}
import com.google.inject.Guice
import spray.can.Http

object VaultKeeperBoot extends App {
  implicit val system = ActorSystem("vaultkeeper-system")

  val injector = Guice.createInjector(new VaultKeeperModule(system))

  val actorProvider = injector.getInstance(classOf[VaultKeeperActorProvider])

  val service = actorProvider.serviceActor

  IO(Http) ! Http.Bind(service, "localhost", port = 8080)
}
