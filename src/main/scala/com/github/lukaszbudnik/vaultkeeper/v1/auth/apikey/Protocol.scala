package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

case class ApiKeyAuth(apiKey: String, algorithm: String, context: String, signature: String)

case class ApiKey(apiKey: String)

