package com.github.lukaszbudnik.vaultkeeper.core

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorRefFactory, Props}
import com.google.inject.Injector

class GuiceActorProvider @Inject()(injector: Injector) {

  private def createFromGuice[T](clazz: Class[T]): T = injector.getInstance(clazz)

  def createInstance[T](implicit m: Manifest[T]): T = {
    createFromGuice(m.runtimeClass.asInstanceOf[Class[T]])
  }

  def createActor[T <: Actor](actorRefFactory: ActorRefFactory)(implicit m: Manifest[T]): ActorRef = {
    createActor(actorRefFactory, None)
  }

  def createActor[T <: Actor](actorRefFactory: ActorRefFactory, name: Option[String])(implicit m: Manifest[T]): ActorRef = {
    createActor(actorRefFactory, name, (p: Props) => p)
  }

  def createActor[T <: Actor](actorRefFactory: ActorRefFactory, name: Option[String], propsOptions: Props => Props)(implicit m: Manifest[T]): ActorRef = {
    name.map({ n =>
      actorRefFactory.actorOf(propsOptions(Props(createInstance(m))), n)
    }).getOrElse({
      actorRefFactory.actorOf(propsOptions(Props(createInstance(m))))
    })
  }

}
