package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

import javax.inject.Inject

import akka.actor.Actor

class ApiKeyAuthActor @Inject()(apiKeyService: ApiKeyAuthService) extends Actor {

   def receive = _ match {

     case authenticationRequest: ApiKeyAuth => {
       sender ! apiKeyService.authenticate(authenticationRequest)
     }

   }

 }
