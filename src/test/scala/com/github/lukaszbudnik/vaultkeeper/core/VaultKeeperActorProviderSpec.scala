package com.github.lukaszbudnik.vaultkeeper.core

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.Guice
import org.junit.runner.RunWith
import org.mockito.Matchers.{anyString => isAnyString, eq => isEq}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner])
class VaultKeeperActorProviderSpec extends Specification with Mockito {

  val system = ActorSystem("actor-system-test")

  val injector = Guice.createInjector(new VaultKeeperModule(system))

  val actorProvider = injector.getInstance(classOf[VaultKeeperActorProvider])

  "VaultKeeperActorProvider" should {

    "provide instance of KeyActor" in {
      val keyActor = actorProvider.keyActor
      keyActor must beAnInstanceOf[ActorRef]
    }

    "provide instance of ApiKeyAuthActor" in {
      val apiKeyAuthActor = actorProvider.apiKeyAuthActor
      apiKeyAuthActor must beAnInstanceOf[ActorRef]
    }

    "provide instance of MngmntAuthActor" in {
      val mngmntAuthActor = actorProvider.mngmntAuthActor
      mngmntAuthActor must beAnInstanceOf[ActorRef]
    }

    "provide instance of ServiceActor" in {
      val serviceActor = actorProvider.serviceActor
      serviceActor must beAnInstanceOf[ActorRef]
    }

  }

}
