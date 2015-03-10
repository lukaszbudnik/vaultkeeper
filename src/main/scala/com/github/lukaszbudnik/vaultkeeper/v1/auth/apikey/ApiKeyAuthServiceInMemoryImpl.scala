package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

import javax.inject.Singleton

import org.apache.commons.codec.digest.HmacUtils

@Singleton
class ApiKeyAuthServiceInMemoryImpl extends ApiKeyAuthService {

  private case class InMemoryApiKey(apiKey: String, secretKey: String)

  private var apiKeys: Seq[InMemoryApiKey] = Seq(InMemoryApiKey("0123456789", "9876543210"))

  override def authenticate(authenticationRequest: ApiKeyAuth): Option[ApiKey] = {
    apiKeys.find(i => i.apiKey == authenticationRequest.apiKey &&
      HmacUtils.hmacSha256Hex(i.secretKey, authenticationRequest.context) == authenticationRequest.signature).map(i =>
      ApiKey(i.apiKey))
  }


  override def removeApiKey(apiKey: String) = {
    this.synchronized {
      apiKeys = apiKeys.filterNot(_.apiKey == apiKey)
    }
  }

}
