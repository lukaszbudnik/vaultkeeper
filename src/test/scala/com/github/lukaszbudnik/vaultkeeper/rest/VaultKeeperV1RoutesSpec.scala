package com.github.lukaszbudnik.vaultkeeper.rest

import java.util.UUID

import com.github.lukaszbudnik.vaultkeeper.auth.{User, MngmntUserStoreService}
import com.github.lukaszbudnik.vaultkeeper.keys.{KeyStoreService, KeyStoreServiceImpl}
import com.github.lukaszbudnik.vaultkeeper.utils.{PemUtils, SignatureUtils}
import com.google.inject.{AbstractModule, Guice}
import org.apache.commons.codec.digest.HmacUtils
import org.apache.commons.io.IOUtils
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.mockito.Matchers.{ eq => isEq }
import spray.http.HttpHeaders.`Remote-Address`
import spray.http._
import spray.testkit.Specs2RouteTest

import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class VaultKeeperV1RoutesSpec extends Specification with Mockito with Specs2RouteTest with JsonProtocol {

  val apiKey = UUID.randomUUID.toString
  val context = UUID.randomUUID.toString
  val signature = HmacUtils.hmacSha256Hex(apiKey, context)

  val user = "lukasz"
  val password = "budnik"
  val apiKey = UUID.randomUUID.toString
  val context = UUID.randomUUID.toString
  val signature = HmacUtils.hmacSha256Hex(apiKey, context)

  val keyName = "existing"
  val keyContent = "keyContent"



  val mockMngmntUserStoreService = {
    val mock: MngmntUserStoreService = mock[MngmntUserStoreService]
    mock.authenticate(isEq(user), isEq(password)) returns Some(User(user, apiKey))
    mock
  }

//  val injector = Guice.createInjector(new AbstractModule {
//    override def configure(): Unit = {
//      bind(classOf[MngmntUserStoreService]).toInstance(mockMngmntUserStoreService)
//      bind(classOf[KeyStoreService]).to(classOf[KeyStoreServiceImpl])
//    }
//  })

  val restRoutes = new VaultKeeperV1Routes {
    // return the Spec2RouteTest system
    def actorRefFactory = system

    val mngmntUserStoreService: MngmntUserStoreService = mockMngmntUserStoreService

    override def getKey(name: String): Future[Option[String]] = Future(Some(keyContent))
  }

  val credentials = BasicHttpCredentials("lukasz", "budnik")

  "The service V1" should {

    "add new key and confirm that it was registered" in {
      Post("/api/v1/mngmnt/keys", Key("new", "test", Nil)) ~> addHeader(`Remote-Address`("1.2.3.4")) ~>
        addHeader(`X-VaultKeeper-Context`("abc")) ~>
        addCredentials(credentials) ~> restRoutes.vaultKeeperV1Routes ~> check {
        status === StatusCodes.OK
        body.contentType === ContentTypes.`application/json`
        responseAs[Option[Registered]] must beSome.which {
          case Registered(result) if result == true => true
        }
      }
    }

    "get key" in {
      Get("/api/v1/keys/" + keyName) ~> addHeader(`Remote-Address`("1.2.3.4")) ~>
        addHeader(`X-VaultKeeper-Context`(context)) ~>
        addHeader(`X-VaultKeeper-Signature`(signature)) ~>
        addHeader(`X-VaultKeeper-Algorithm`("asas")) ~>
        addHeader(`X-VaultKeeper-Credentials`(apiKey)) ~>
        restRoutes.vaultKeeperV1Routes ~> check {
        status === StatusCodes.OK
        body.contentType === ContentTypes.`application/json`
        responseAs[Option[Key]] must beSome.which {
          case Key(name, content, credentials) => {
            name === keyName
            content === keyContent
            credentials === Seq(Credentials(vaultCredentials))
          }
        }
      }
    }

  }
}

