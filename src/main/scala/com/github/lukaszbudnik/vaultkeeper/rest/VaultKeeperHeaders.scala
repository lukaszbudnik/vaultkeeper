package com.github.lukaszbudnik.vaultkeeper.rest

import spray.http.HttpHeader
import spray.http.HttpHeaders.RawHeader


object `X-VaultKeeper-Algorithm` {
  lazy val headerName = "X-VaultKeeper-Algorithm"
  def apply(headerValue: String): HttpHeader = RawHeader(headerName, headerValue)
}

object `X-VaultKeeper-Key` {
  lazy val headerName = "X-VaultKeeper-Key"
  def apply(headerValue: String): HttpHeader = RawHeader(headerName, headerValue)
}

object `X-VaultKeeper-Context` {
  lazy val headerName = "X-VaultKeeper-Context"
  def apply(headerValue: String): HttpHeader = RawHeader(headerName, headerValue)
}

object `X-VaultKeeper-Signature` {
  lazy val headerName = "X-VaultKeeper-Signature"
  def apply(headerValue: String): HttpHeader = RawHeader(headerName, headerValue)
}
