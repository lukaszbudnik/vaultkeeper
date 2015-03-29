package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestActorRef
import com.github.lukaszbudnik.vaultkeeper.testkit.VaultKeeperTestKit
import org.junit.runner.RunWith
import org.mockito.Matchers.{anyString => isAnyString, eq => isEq}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ApiAuthActorSpec extends Specification with Mockito {

  val auth = ApiKeyAuth("apiKey", "algorithm", "context", "signature")
  val authWrongApiKey = ApiKeyAuth("wrongApiKey", "algorithm", "context", "signature")
  val apiKeyAdd = ApiKeyAdd("apiKey", "secretKey")
  val apiKey = ApiKey("apiKey")

  val mockApiKeyAuthService = {
    val m: ApiKeyAuthService = mock[ApiKeyAuthService]
    m.add(apiKeyAdd) returns apiKey
    m.authenticate(auth) returns Some(apiKey)
    m.authenticate(authWrongApiKey) returns None
    m
  }

  "ApiAuthActor" should {
    "add api key" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[ApiKeyAuthActor](Props(new ApiKeyAuthActor(mockApiKeyAuthService)))
      actorRef ! apiKeyAdd
      expectMsg(apiKey)

      there was one(mockApiKeyAuthService).add(apiKeyAdd)
    }

    "authenticate api key when auth request is correct" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[ApiKeyAuthActor](Props(new ApiKeyAuthActor(mockApiKeyAuthService)))
      actorRef ! auth
      expectMsg(Some(apiKey))

      there was one(mockApiKeyAuthService).authenticate(auth)
    }

    "not authenticate api key when auth request is wrong" in new VaultKeeperTestKit(ActorSystem("actor-system-test")) {
      val actorRef = TestActorRef[ApiKeyAuthActor](Props(new ApiKeyAuthActor(mockApiKeyAuthService)))
      actorRef ! authWrongApiKey
      expectMsg(None)

      there was one(mockApiKeyAuthService).authenticate(authWrongApiKey)
    }

  }

}
