package com.github.lukaszbudnik.vaultkeeper.v1.rest

import akka.pattern.ask
import akka.util.Timeout
import com.github.lukaszbudnik.vaultkeeper.v1.auth.{Credentials, MngmntAuthenticator}
import org.json4s.{DefaultFormats, FieldSerializer}
import org.slf4j.{LoggerFactory, MDC}
import spray.httpx.Json4sJacksonSupport
import spray.routing.HttpService
import spray.routing.authentication.BasicAuth

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

case class Registered(success: Boolean)

case class Key(name: String, content: String, credentials: Seq[Credentials])

object RestJsonProtocol extends Json4sJacksonSupport {
  lazy val json4sJacksonFormats = DefaultFormats +
    FieldSerializer[Registered]() +
    FieldSerializer[Key]()
}

trait VaultKeeperV1Routes extends HttpService with MngmntAuthenticator with JsonProtocol {

  val log = LoggerFactory.getLogger(classOf[VaultKeeperV1Routes])

  def getKey(name: String): Future[Option[String]] = {
    implicit val timeout = Timeout(2.seconds)
    val keyStoreActor = actorRefFactory.actorSelection("/user/vaultkeeper-keystore")
    val key: Future[Option[String]] = (keyStoreActor ? name).mapTo[Option[String]]
    key
  }

  val vaultKeeperV1Routes =
    (headerValueByName("Remote-Address") & headerValueByName(`X-VaultKeeper-Context`.headerName)) { (remoteAddress, context) => {

      MDC.put("context", context)

      pathPrefix("api" / "v1") {
        pathPrefix("mngmnt") {
          authenticate(BasicAuth(userPassAuthenticator _, "mngmnt api")) { user =>
            path("keys") {
              post {
                entity(as[Key]) { key =>
                  log.info(s"registering $key for user $user")
                  complete(Registered(true))
                }
              }
            }
          }
        } ~ (pathPrefix("keys")) {
          (headerValueByName("X-VaultKeeper-Algorithm") & headerValueByName("X-VaultKeeper-Key") & headerValueByName("X-VaultKeeper-Signature")) { (algorithm, credentials, signature) =>

            (path(Segment) & get) { name => {

              //                val (isValid, errorMessage) = isValidType(algorithm, credentials, context, signature)

              validate(true, "Request is not valid: $errorMessage") {

                log.info(s" custom headers $context $signature")

                val key = getKey(name)

                complete(key map { key =>
                  key match {
                    case Some(content) => Some(Key(name, content, Seq(Credentials(credentials))))
                    case _ => None
                  }

                })
              }
            }
            }
          }
        }
      }
    }
    }

  def isValidType(algorithm: String, credentials: String, context: String, signature: String): Boolean = {
    if (algorithm != "VaultKeeper-SHA512-PRIVATE-KEY-SIGNED") {
      throw new IllegalArgumentException(s"Unknown algorithm $algorithm")
    }




    true
  }


}