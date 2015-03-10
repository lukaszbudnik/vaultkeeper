package com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestActorRef
import com.github.lukaszbudnik.vaultkeeper.testkit.VaultKeeperTestKit
import org.junit.runner.RunWith
import org.mockito.Matchers.{anyString => isAnyString, eq => isEq}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MngmntAuthActorSpec extends Specification with Mockito {

  val auth = UserAuth("user", "pass")
  val authWrongApiKey = UserAuth("x", "pass")
  val user = User("apiKey")

  val userAdd = UserAdd("newuser", "newpass")
  val userRemove = UserRemove("removed")

  val mockApiKeyAuthService = {
    val m: MngmntUserAuthService = mock[MngmntUserAuthService]
    m.authenticate(auth.username, auth.password) returns Some(user)
    m.authenticate(authWrongApiKey.username, authWrongApiKey.password) returns None
    m
  }

  "MngmntAuthActor" should {
    "authenticate user when auth request is correct" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[MngmntAuthActor](Props(new MngmntAuthActor(mockApiKeyAuthService)))
      actorRef ! auth
      expectMsg(Some(user))

      there was one(mockApiKeyAuthService).authenticate(auth.username, auth.password)
    }

    "not authenticate user when auth request is wrong" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[MngmntAuthActor](Props(new MngmntAuthActor(mockApiKeyAuthService)))
      actorRef ! authWrongApiKey
      expectMsg(None)

      there was one(mockApiKeyAuthService).authenticate(authWrongApiKey.username, authWrongApiKey.password)
    }

    "add new user" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[MngmntAuthActor](Props(new MngmntAuthActor(mockApiKeyAuthService)))
      actorRef ! userAdd
      expectMsg(UserAdded())

      there was one(mockApiKeyAuthService).add(userAdd.username, userAdd.password)
    }

    "remove user" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[MngmntAuthActor](Props(new MngmntAuthActor(mockApiKeyAuthService)))
      actorRef ! userRemove
      expectMsg(UserRemoved())

      there was one(mockApiKeyAuthService).remove(userRemove.username)
    }

  }

}
