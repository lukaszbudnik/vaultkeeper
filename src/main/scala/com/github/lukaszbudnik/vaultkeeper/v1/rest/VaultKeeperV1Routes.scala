package com.github.lukaszbudnik.vaultkeeper.v1.rest

import akka.pattern.ask
import akka.util.Timeout
import com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey.{ApiKeyAdd, ApiKey, ApiKeyAuth}
import com.github.lukaszbudnik.vaultkeeper.v1.auth.auth
import com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt.{UserAuth, User}
import com.github.lukaszbudnik.vaultkeeper.v1.keys._
import org.json4s.{DefaultFormats, FieldSerializer}
import org.slf4j.{LoggerFactory, MDC}
import spray.can.Http
import spray.http.{StatusCodes, StatusCode}
import spray.httpx.Json4sJacksonSupport
import spray.routing.authentication._
import spray.routing.{AuthenticationFailedRejection, HttpService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object RestJsonProtocol extends Json4sJacksonSupport {
  lazy val json4sJacksonFormats = DefaultFormats +
    FieldSerializer[KeyAdd]() +
    FieldSerializer[KeyAdded]()
}


trait VaultKeeperV1Routes extends HttpService with JsonProtocol {

  implicit val timeout = Timeout(2.seconds)

  val log = LoggerFactory.getLogger(classOf[VaultKeeperV1Routes])

  def validateRequest(algorithm: String): Boolean = algorithm == auth.HMAC_ALGORITHM

  def addKey(keyAdd: KeyAdd): Future[KeyAdded] = {
    val keyActor = actorRefFactory.actorSelection("/user/vaultkeeper-keyactor")
    val keyAdded: Future[KeyAdded] = (keyActor ? keyAdd).mapTo[KeyAdded]
    keyAdded
  }

  def updateKey(keyUpdate: KeyUpdate): Future[KeyUpdated] = {
    val keyActor = actorRefFactory.actorSelection("/user/vaultkeeper-keyactor")
    val keyUpdated: Future[KeyUpdated] = (keyActor ? keyUpdate).mapTo[KeyUpdated]
    keyUpdated
  }

  def getKey(keyName: String): Future[Option[Key]] = {
    val keyActor = actorRefFactory.actorSelection("/user/vaultkeeper-keyactor")
    val key: Future[Option[Key]] = (keyActor ? KeyGet(keyName)).mapTo[Option[Key]]
    key
  }

  def getKeyMetaData(keyName: String): Future[Option[KeyMetaData]] = {
    val keyActor = actorRefFactory.actorSelection("/user/vaultkeeper-keyactor")
    val key: Future[Option[KeyMetaData]] = (keyActor ? KeyGetMetaData(keyName)).mapTo[Option[KeyMetaData]]
    key
  }

  def authenticateMngmnt(userPass: Option[UserPass]): Future[Option[User]] = {
    userPass match {
      case Some(userPass) => {
        val apiKeyAuthActor = actorRefFactory.actorSelection("/user/vaultkeeper-mngmntactor")
        val authResponse: Future[Option[User]] = (apiKeyAuthActor ? UserAuth(userPass.user, userPass.pass)).mapTo[Option[User]]
        authResponse
      }
      case _ => Future(None)
    }
  }

  def authenticateApiKey(request: ApiKeyAuth): Future[Option[ApiKey]] = {
    val apiKeyAuthActor = actorRefFactory.actorSelection("/user/vaultkeeper-apiauthactor")
    val authResponse: Future[Option[ApiKey]] = (apiKeyAuthActor ? request).mapTo[Option[ApiKey]]
    authResponse
  }

  def addApiKey(apiKeyAdd: ApiKeyAdd): Future[ApiKey] = {
    val apiKeyAuthActor = actorRefFactory.actorSelection("/user/vaultkeeper-apiauthactor")
    val apiKey: Future[ApiKey] = (apiKeyAuthActor ? apiKeyAdd).mapTo[ApiKey]
    apiKey
  }

  val vaultKeeperV1Routes =
    (headerValueByName("Remote-Address") & headerValueByName(`X-VaultKeeper-Context`.headerName)) { (remoteAddress, context) => {

      MDC.put("context", context)

      pathPrefix("api" / "v1") {
        pathPrefix("mngmnt") {
          authenticate(BasicAuth(authenticateMngmnt _, "mngmnt api")) { user =>
            pathPrefix("apikeys") {
              post {
                entity(as[ApiKeyAdd]) { apiKeyAdd =>
                  log.info(s"$user registered new api key ${apiKeyAdd.apiKey}")
                  val apiKey = addApiKey(apiKeyAdd)
                  complete(apiKey)
                }
              }
            } ~
            pathPrefix("keys") {
              post {
                  entity(as[KeyAdd]) { keyAdd =>
                    log.info(s"$user registered ${keyAdd.key.keyName} for api keys ${keyAdd.credentials}")
                    complete(addKey(keyAdd))
                  }
                } ~ (put & path(Segment / "credentials")) { keyName =>
                  entity(as[Seq[ApiKey]]) { credentials =>
                    log.info(s"$user updated ${keyName} credentials with api keys ${credentials}")

                    complete(updateKey(KeyUpdate(keyName, None, Some(credentials))))
                  }
                } ~ (put & path(Segment / "isActive")) { keyName =>
                entity(as[Boolean]) { isActive =>
                  log.info(s"$user updated ${keyName} credentials with isActive ${isActive}")

                  complete(updateKey(KeyUpdate(keyName, Some(isActive), None)))
                }
              } ~ (get & path(Segment)) { keyName =>
                  complete(getKeyMetaData(keyName))
              }
              }
          }
        } ~ (pathPrefix("keys")) {
          (headerValueByName(`X-VaultKeeper-Algorithm`.headerName) & headerValueByName(`X-VaultKeeper-Credentials`.headerName) &
            headerValueByName(`X-VaultKeeper-Signature`.headerName)) { (algorithm, credentials, signature) =>

            (path(Segment) & get) { name => {

              validate(validateRequest(algorithm), s"Unknown algorithm $algorithm") {

                val authResponse = authenticateApiKey(ApiKeyAuth(credentials, algorithm, context, signature))

                val response = authResponse map { authReponse =>
                  authReponse match {
                    case Some(apiKey: ApiKey) => {
                      log.info(s"Api key $credentials successfully authenticated")

                      val keyFuture = getKey(name)
                      keyFuture
                    }
                    case _ => {
                      log.info(s"Authentication failed for $credentials")
                      Future(AuthenticationFailedRejection(AuthenticationFailedRejection.CredentialsRejected, Nil))
                    }
                  }

                }

                complete(response)
              }
            }
            }
          }
        }
      }
    }
    }

}