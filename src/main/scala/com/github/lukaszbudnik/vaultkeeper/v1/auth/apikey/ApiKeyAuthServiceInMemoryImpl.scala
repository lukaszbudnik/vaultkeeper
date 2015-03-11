package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

import javax.inject.Singleton

import org.apache.commons.codec.digest.HmacUtils

@Singleton
class ApiKeyAuthServiceInMemoryImpl extends ApiKeyAuthService {

  private[apikey] case class InMemoryApiKey(apiKey: String, secretKey: String)

  private[apikey] var apiKeys: Seq[InMemoryApiKey] = Nil

  override def authenticate(authenticationRequest: ApiKeyAuth): Option[ApiKey] = {
    this.synchronized {
      apiKeys.find(i => i.apiKey == authenticationRequest.apiKey &&
        HmacUtils.hmacSha256Hex(i.secretKey, authenticationRequest.context) == authenticationRequest.signature).map(i =>
        ApiKey(i.apiKey))
    }
  }

  override def add(apiKey: String, secretKey: String): ApiKey = {
    this.synchronized {
      apiKeys = apiKeys :+ InMemoryApiKey(apiKey, secretKey)
      ApiKey(apiKey)
    }
  }

  override def update(apiKey: String, secretKey: String): Option[ApiKey] = {
    this.synchronized {
      val foundApiKey = apiKeys.find(_.apiKey == apiKey)
      foundApiKey map { foundApiKey =>
        remove(apiKey)
        add(apiKey, secretKey)
      }
    }
  }

  override def remove(apiKey: String) = {
    this.synchronized {
      apiKeys = apiKeys.filterNot(_.apiKey == apiKey)
    }
  }

}
