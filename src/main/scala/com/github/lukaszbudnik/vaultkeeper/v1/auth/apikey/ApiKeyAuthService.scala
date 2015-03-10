package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

trait ApiKeyAuthService {

  def removeApiKey(apiKey: String)

  def authenticate(authenticationRequest: ApiKeyAuth): Option[ApiKey]

}
