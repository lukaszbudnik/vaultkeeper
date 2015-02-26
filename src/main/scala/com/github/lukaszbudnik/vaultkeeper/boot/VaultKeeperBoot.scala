package com.github.lukaszbudnik.vaultkeeper

import akka.actor.ActorSystem
import akka.io.IO
import com.github.lukaszbudnik.vaultkeeper.core.GuiceActorProvider
import com.github.lukaszbudnik.vaultkeeper.v1.auth.{MngmntUserStoreService, MngmntUserStoreServiceImpl}
import com.github.lukaszbudnik.vaultkeeper.v1.keys.{KeyStoreActor, KeyStoreService, KeyStoreServiceImpl}
import com.github.lukaszbudnik.vaultkeeper.v1.rest.VaultKeeperServiceActor
import com.google.inject.{AbstractModule, Guice}
import spray.can.Http

object VaultKeeperBoot extends App {

  implicit val system = ActorSystem("vaultkeeper-system")

  val injector = Guice.createInjector(new AbstractModule {
    override def configure(): Unit = {
      bind(classOf[MngmntUserStoreService]).to(classOf[MngmntUserStoreServiceImpl])
      bind(classOf[KeyStoreService]).to(classOf[KeyStoreServiceImpl])
    }
  })

  val actorProvider = injector.getInstance(classOf[GuiceActorProvider])

  val service = actorProvider.create[VaultKeeperServiceActor](Some("vaultkeeper-service"), system)

  val keyStore = actorProvider.create[KeyStoreActor](Some("vaultkeeper-keystore"), system)

  IO(Http) ! Http.Bind(service, "localhost", port = 8080)
}
