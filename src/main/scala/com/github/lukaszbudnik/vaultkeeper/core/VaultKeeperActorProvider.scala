package com.github.lukaszbudnik.vaultkeeper.core

import javax.inject.{Singleton, Inject}

import akka.actor.ActorSystem
import com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey.ApiKeyAuthActor
import com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt.MngmntAuthActor
import com.github.lukaszbudnik.vaultkeeper.v1.keys.KeyActor
import com.github.lukaszbudnik.vaultkeeper.v1.rest.VaultKeeperServiceActor

@Singleton
class VaultKeeperActorProvider @Inject() (actorProvider: GuiceActorProvider, system: ActorSystem) {

  val serviceActor = actorProvider.createActor[VaultKeeperServiceActor](system, Some("vaultkeeper-serviceactor"))

  val keyActor = actorProvider.createActor[KeyActor](system, Some("vaultkeeper-keyactor"))

  val apiKeyAuthActor = actorProvider.createActor[ApiKeyAuthActor](system, Some("vaultkeeper-apiauthactor"))

  val mngmntAuthActor = actorProvider.createActor[MngmntAuthActor](system, Some("vaultkeeper-mngmntactor"))

}
