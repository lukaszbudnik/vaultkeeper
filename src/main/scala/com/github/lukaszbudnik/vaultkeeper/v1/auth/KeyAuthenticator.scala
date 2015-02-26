package com.github.lukaszbudnik.vaultkeeper.v1.auth

case class Credentials(apiKey: String)

trait KeyAuthenticator {

  def authenticate(apiKey: String, context: String, signature: String): Option[Credentials]

}
