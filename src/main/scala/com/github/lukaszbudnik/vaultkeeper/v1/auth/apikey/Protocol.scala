package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

case class ApiKey(apiKey: String)

case class ApiKeyAdd(apiKey: String, secretKey: String)

case class ApiKeyAuth(apiKey: String, algorithm: String, context: String, signature: String)


