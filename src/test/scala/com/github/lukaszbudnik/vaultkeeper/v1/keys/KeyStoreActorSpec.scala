package com.github.lukaszbudnik.vaultkeeper.v1.keys

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestActorRef
import com.github.lukaszbudnik.vaultkeeper.keys.{KeyStoreActor, KeyStoreService}
import com.github.lukaszbudnik.vaultkeeper.testkit.VaultKeeperTestKit
import org.junit.runner.RunWith
import org.mockito.Matchers.{eq => isEq, anyString => isAnyString}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class KeyStoreActorSpec extends Specification with Mockito {

  val keyName = "abc"
  val keyContent = Some("abc123")

  val mockKeyStoreService = {
    val m: KeyStoreService = mock[KeyStoreService]
    m.get(isAnyString) returns None
    m.get(isEq(keyName)) returns keyContent
    m
  }

  "KeyStoreActor" should {
    "send back a key when found in key store service" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[KeyStoreActor](Props(new KeyStoreActor(mockKeyStoreService)))
      actorRef ! keyName
      expectMsg(keyContent)

      there was one(mockKeyStoreService).get(keyName)
    }

    "send back None when not found in key store service" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[KeyStoreActor](Props(new KeyStoreActor(mockKeyStoreService)))
      actorRef ! (keyName + keyName)
      expectMsg(None)

      there was one(mockKeyStoreService).get(keyName + keyName)
    }


  }

}
