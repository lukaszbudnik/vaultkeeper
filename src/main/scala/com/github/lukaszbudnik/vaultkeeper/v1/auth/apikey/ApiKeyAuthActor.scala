package com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey

import javax.inject.Inject

import akka.actor.Actor

class ApiKeyAuthActor @Inject()(apiKeyService: ApiKeyAuthService) extends Actor {

   def receive = _ match {

     case apiKeyAdd: ApiKeyAdd => {
       sender ! apiKeyService.add(apiKeyAdd)
     }

     case apiKeyAuth: ApiKeyAuth => {
       sender ! apiKeyService.authenticate(apiKeyAuth)
     }

   }

 }
