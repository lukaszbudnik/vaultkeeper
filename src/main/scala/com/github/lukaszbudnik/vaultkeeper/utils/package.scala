package com.github.lukaszbudnik.vaultkeeper

import java.security.Security

import org.bouncycastle.jce.provider.BouncyCastleProvider

package object utils {

  Security.addProvider(new BouncyCastleProvider)

  lazy val RSA_ALGORITHM = "RSA"

}
