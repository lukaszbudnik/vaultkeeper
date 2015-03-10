package com.github.lukaszbudnik.vaultkeeper

import akka.actor.ActorSystem
import akka.io.IO
import com.github.lukaszbudnik.vaultkeeper.core.GuiceActorProvider
import com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey.{ApiKeyAuthActor, ApiKeyAuthServiceInMemoryImpl, ApiKeyAuthService}
import com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt.{MngmntAuthActor, MngmntUserAuthService, MngmntUserAuthServiceInMemoryImpl}
import com.github.lukaszbudnik.vaultkeeper.v1.keys.{KeyActor, KeyService, KeyServiceInMemoryImpl}
import com.github.lukaszbudnik.vaultkeeper.v1.rest.VaultKeeperServiceActor
import com.google.inject.{AbstractModule, Guice}
import spray.can.Http

object VaultKeeperBoot extends App {

  implicit val system = ActorSystem("vaultkeeper-system")

  val injector = Guice.createInjector(new AbstractModule {
    override def configure(): Unit = {
      bind(classOf[MngmntUserAuthService]).to(classOf[MngmntUserAuthServiceInMemoryImpl])
      bind(classOf[ApiKeyAuthService]).to(classOf[ApiKeyAuthServiceInMemoryImpl])
      bind(classOf[KeyService]).to(classOf[KeyServiceInMemoryImpl])
    }
  })

  val actorProvider = injector.getInstance(classOf[GuiceActorProvider])

  val service = actorProvider.create[VaultKeeperServiceActor](Some("vaultkeeper-service"), system)

  val keyActor = actorProvider.create[KeyActor](Some("vaultkeeper-keyactor"), system)

  val apiKeyAuthActor = actorProvider.create[ApiKeyAuthActor](Some("vaultkeeper-apiauthactor"), system)

  val mngmntAuthActor = actorProvider.create[MngmntAuthActor](Some("vaultkeeper-mngmntactor"), system)

  IO(Http) ! Http.Bind(service, "localhost", port = 8080)
}
