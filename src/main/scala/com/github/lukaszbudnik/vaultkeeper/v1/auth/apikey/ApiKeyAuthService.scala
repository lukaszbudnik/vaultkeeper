package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

trait ApiKeyAuthService {

  def add(apiKey: String, secretKey: String): ApiKey

  def update(apiKey: String, secretKey: String): Option[ApiKey]

  def remove(apiKey: String)

  def authenticate(authenticationRequest: ApiKeyAuth): Option[ApiKey]

}
