package com.github.lukaszbudnik.vaultkeeper.core

import akka.actor.ActorSystem
import com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey.{ApiKeyAuthServiceInMemoryImpl, ApiKeyAuthService}
import com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt.{MngmntAuthServiceInMemoryImpl, MngmntAuthService}
import com.github.lukaszbudnik.vaultkeeper.v1.keys.{KeyServiceInMemoryImpl, KeyService}
import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory

class VaultKeeperModule(system: ActorSystem) extends AbstractModule {

  lazy val config = ConfigFactory.load

  override def configure(): Unit = {

    val keyServiceClass = config.getString("vaultkeeper.services.keyService")

    bind(classOf[MngmntAuthService]).to(classOf[MngmntAuthServiceInMemoryImpl])
    bind(classOf[ApiKeyAuthService]).to(classOf[ApiKeyAuthServiceInMemoryImpl])
    bind(classOf[KeyService]).to(Class.forName(keyServiceClass).asInstanceOf[Class[_ <: KeyService]])
    bind(classOf[ActorSystem]).toInstance(system)
  }
}
