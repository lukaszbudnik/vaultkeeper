package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

trait ApiKeyAuthService {

  def add(apiKeyAdd: ApiKeyAdd): ApiKey

  def update(apiKey: String, secretKey: String): Option[ApiKey]

  def remove(apiKey: String)

  def authenticate(apiKeyAuth: ApiKeyAuth): Option[ApiKey]

}
