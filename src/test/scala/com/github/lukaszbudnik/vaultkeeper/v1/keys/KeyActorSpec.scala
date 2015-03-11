package com.github.lukaszbudnik.vaultkeeper.v1.keys

import java.util.Date

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestActorRef
import com.github.lukaszbudnik.vaultkeeper.testkit.VaultKeeperTestKit
import org.junit.runner.RunWith
import org.mockito.Matchers.{anyString => isAnyString, eq => isEq}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class KeyActorSpec extends Specification with Mockito {

  val keyName = "abc"
  val key = Key(keyName, "content")
  val keyMetaData = KeyMetaData(keyName, true, new Date, Nil)

  val mockKeyStoreService = {
    val m: KeyService = mock[KeyService]
    m.get(isAnyString) returns None
    m.get(isEq(keyName)) returns Some(key)
    m.getMetaData(isEq(keyName)) returns Some(keyMetaData)
    m
  }

  "KeyStoreActor" should {
    "return key when found" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[KeyActor](Props(new KeyActor(mockKeyStoreService)))
      actorRef ! KeyGet(keyName)
      expectMsg(Some(key))

      there was one(mockKeyStoreService).get(keyName)
    }

    "return None when not found" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[KeyActor](Props(new KeyActor(mockKeyStoreService)))
      actorRef ! KeyGet(new String)
      expectMsg(None)

      there was one(mockKeyStoreService).get(new String)
    }

    "return key meta data when found" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[KeyActor](Props(new KeyActor(mockKeyStoreService)))
      actorRef ! KeyGetMetaData(keyName)
      expectMsg(Some(keyMetaData))

      there was one(mockKeyStoreService).get(keyName)
    }

    "add key" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[KeyActor](Props(new KeyActor(mockKeyStoreService)))
      val keyAdd = KeyAdd(key, Nil)
      actorRef ! keyAdd
      expectMsg(KeyAdded())

      there was one(mockKeyStoreService).add(keyAdd)
    }

    "update key" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[KeyActor](Props(new KeyActor(mockKeyStoreService)))
      val keyUpdate = KeyUpdate(keyName, Some(true), None)
      actorRef ! keyUpdate
      expectMsg(KeyUpdated())

      there was one(mockKeyStoreService).update(keyUpdate)
    }

    "remove key" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[KeyActor](Props(new KeyActor(mockKeyStoreService)))
      actorRef ! KeyRemove(keyName)
      expectMsg(KeyRemoved())

      there was one(mockKeyStoreService).remove(keyName)
    }

  }

}
