package com.github.lukaszbudnik.vaultkeeper.core

import javax.inject.Inject

import akka.actor.{Props, ActorRef, Actor, ActorSystem}
import akka.testkit.TestActorRef
import com.github.lukaszbudnik.vaultkeeper.v1.keys.{KeyService, KeyServiceInMemoryImpl}
import com.google.inject.Guice
import org.junit.runner.RunWith
import org.mockito.Matchers.{anyString => isAnyString, eq => isEq}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

private class DummyActor @Inject() (keyServiceInMemory: KeyServiceInMemoryImpl) extends Actor {
  override def receive: Receive = ???
}

@RunWith(classOf[JUnitRunner])
class GuiceActorProviderSpec extends Specification with Mockito {

  val system = ActorSystem("actor-system-test")

  val injector = Guice.createInjector()

  val actorProvider = injector.getInstance(classOf[GuiceActorProvider])

  "GuiceActorProvider" should {
    "create instance directly from Guice" in {
      val service = actorProvider.createInstance[KeyServiceInMemoryImpl]
      service must beAnInstanceOf[KeyService]
    }

    "create instance of actor" in {
      val actorRef = actorProvider.createActor(system)
      actorRef must beAnInstanceOf[ActorRef]
    }

    "create instance of actor using props" in {
      val actorRef = actorProvider.createActor(system, Some("named-with-props"), (p: Props) => Props(new DummyActor(new KeyServiceInMemoryImpl)))
      actorRef must beAnInstanceOf[ActorRef]
    }

  }

}
