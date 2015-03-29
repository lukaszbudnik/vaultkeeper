package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

import com.github.lukaszbudnik.vaultkeeper.v1.auth.auth.HMAC_ALGORITHM
import org.apache.commons.codec.digest.HmacUtils
import org.junit.runner.RunWith
import org.mockito.Matchers.{anyString => isAnyString, eq => isEq}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ApiKeyAuthServiceInMemorySpec extends Specification with Mockito {

  sequential

  val apiKeyAuthService = new ApiKeyAuthServiceInMemoryImpl

  val apiKey = "0123456789"
  val secretKey = "987654321"

  "ApiKeyAuthServiceInMemoryImpl" should {

    "add api key" in {
      apiKeyAuthService.add(ApiKeyAdd(apiKey, apiKey))

      apiKeyAuthService.apiKeys must haveSize(1)
    }

    "update existing api key" in {
      apiKeyAuthService.update(apiKey, secretKey)

      apiKeyAuthService.apiKeys must haveSize(1)
    }

    "update non-existing api key" in {
      val result = apiKeyAuthService.update(secretKey, secretKey)

      result must beNone
    }

    "authenticate if signature is correct" in {
      val context = "context"
      val signature = HmacUtils.hmacSha256Hex(secretKey, context)
      val apiKeyAuth = ApiKeyAuth(apiKey, HMAC_ALGORITHM, context, signature)
      val ok = apiKeyAuthService.authenticate(apiKeyAuth)
      ok must beSome.which {
        _ match {
          case ApiKey(ok) => ok === apiKey
        }
      }
    }

    "not authenticate if signature is not correct" in {
      val context = "context"
      val signature = HmacUtils.hmacSha256Hex(apiKey, context)
      val apiKeyAuth = ApiKeyAuth(apiKey, HMAC_ALGORITHM, context, signature)
      val ko = apiKeyAuthService.authenticate(apiKeyAuth)
      ko must beNone
    }

  }

}
