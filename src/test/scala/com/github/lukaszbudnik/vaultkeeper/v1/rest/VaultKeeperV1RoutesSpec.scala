package com.github.lukaszbudnik.vaultkeeper.v1.rest

import java.util.UUID

import akka.actor.ActorSystem
import com.github.lukaszbudnik.vaultkeeper.core.{VaultKeeperActorProvider, VaultKeeperModule}
import com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey.{ApiKeyAdd, ApiKeyAuthService, ApiKey, ApiKeyAuth}
import com.github.lukaszbudnik.vaultkeeper.v1.auth.auth
import com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt.User
import com.github.lukaszbudnik.vaultkeeper.v1.keys._
import com.google.inject.Guice
import org.apache.commons.codec.digest.HmacUtils
import org.junit.runner.RunWith
import org.mockito.Matchers.{eq => isEq}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.HttpHeaders.`Remote-Address`
import spray.http._
import spray.routing.authentication.UserPass
import spray.testkit.Specs2RouteTest

import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class VaultKeeperV1RoutesSpec extends Specification with Specs2RouteTest with JsonProtocol {

  sequential

  lazy val user = "lukasz"
  lazy val password = "budnik"

  lazy val apiKey = UUID.randomUUID.toString
  lazy val secretKey = UUID.randomUUID.toString
  lazy val context = UUID.randomUUID.toString
  lazy val signature = HmacUtils.hmacSha256Hex(secretKey, context)

  lazy val keyName = "existing"
  lazy val keyContent = "keyContent"

  override def createActorSystem(): ActorSystem = {
    val system = ActorSystem("test-vaultkeeper-system")
    val injector = Guice.createInjector(new VaultKeeperModule(system))
    val actorProvider = injector.getInstance(classOf[VaultKeeperActorProvider])
    system
  }

  val restRoutes = new VaultKeeperV1Routes {
    // return the Spec2RouteTest system
    def actorRefFactory = system

//    override def addKey(keyAdd: KeyAdd): Future[KeyAdded] = Future(KeyAdded())
//
//    override def updateKey(keyUpdate: KeyUpdate): Future[KeyUpdated] = Future(KeyUpdated())
//
//    override def getKey(name: String): Future[Option[String]] = Future(Some(keyContent))

//    override def authenticateMngmnt(userPass: Option[UserPass]): Future[Option[User]] = Future(Some(User(user)))

//    override def authenticateApiKey(request: ApiKeyAuth): Future[Option[ApiKey]] = Future(Some(ApiKey(apiKey)))
  }

  val credentials = BasicHttpCredentials("lukasz", "budnik")

  "The service V1" should {

    "add new key and confirm that it was added" in {
      Post("/api/v1/mngmnt/keys", KeyAdd(Key(keyName, keyContent), Nil)) ~> addHeader(`Remote-Address`("1.2.3.4")) ~>
        addHeader(`X-VaultKeeper-Context`("abc")) ~>
        addCredentials(credentials) ~> restRoutes.vaultKeeperV1Routes ~> check {
        status === StatusCodes.OK
        body.contentType === ContentTypes.`application/json`
        responseAs[Option[KeyAdded]] must beSome
      }
    }

    "add new api key and confirm that it was added" in {
      Post("/api/v1/mngmnt/apikeys", ApiKeyAdd(apiKey, secretKey)) ~> addHeader(`Remote-Address`("1.2.3.4")) ~>
        addHeader(`X-VaultKeeper-Context`("abc")) ~>
        addCredentials(credentials) ~> restRoutes.vaultKeeperV1Routes ~> check {
        status === StatusCodes.OK
        body.contentType === ContentTypes.`application/json`
        responseAs[Option[KeyAdded]] must beSome
      }
    }

    "update credentials for existing key" in {
      Put("/api/v1/mngmnt/keys/" + keyName + "/credentials", Seq(ApiKey(apiKey))) ~> addHeader(`Remote-Address`("1.2.3.4")) ~>
        addHeader(`X-VaultKeeper-Context`("abc")) ~>
        addCredentials(credentials) ~> restRoutes.vaultKeeperV1Routes ~> check {
        status === StatusCodes.OK
        body.contentType === ContentTypes.`application/json`
        responseAs[Option[KeyUpdated]] must beSome
      }
    }

    "get key" in {
      Get("/api/v1/keys/" + keyName) ~> addHeader(`Remote-Address`("1.2.3.4")) ~>
        addHeader(`X-VaultKeeper-Context`(context)) ~>
        addHeader(`X-VaultKeeper-Signature`(signature)) ~>
        addHeader(`X-VaultKeeper-Algorithm`(auth.HMAC_ALGORITHM)) ~>
        addHeader(`X-VaultKeeper-Credentials`(apiKey)) ~>
        restRoutes.vaultKeeperV1Routes ~> check {
        status === StatusCodes.OK
        body.contentType === ContentTypes.`application/json`
        responseAs[Option[Key]] must beSome.which {
          case Key(name, content) => {
            name === keyName
            content === keyContent
          }
        }
      }
    }

  }
}

