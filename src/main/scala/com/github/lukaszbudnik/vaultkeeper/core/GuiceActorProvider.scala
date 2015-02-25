package com.github.lukaszbudnik.vaultkeeper.core

import javax.inject.Inject

import akka.actor.{Props, ActorRef, ActorRefFactory, Actor}
import com.google.inject.Injector

class GuiceActorProvider @Inject() (injector: Injector) {

  private def createFromGuice[T <: Actor](clazz: Class[T]): T = injector.getInstance(clazz)

  def create[T <: Actor](implicit m: Manifest[T]): T = {
    createFromGuice(m.runtimeClass.asInstanceOf[Class[T]])
  }

  def create[T <: Actor](actorRefFactory: ActorRefFactory)(implicit m: Manifest[T]): ActorRef = {
    create(None, actorRefFactory, (p: Props) => p)
  }

  def create[T <: Actor](name: Option[String] = None, actorRefFactory: ActorRefFactory)(implicit m: Manifest[T]): ActorRef = {
    create(name, actorRefFactory, (p: Props) => p)
  }

  def create[T <: Actor](name: Option[String], actorRefFactory: ActorRefFactory, propsOptions: Props => Props)(implicit m: Manifest[T]): ActorRef = {
    name.map({ n =>
      actorRefFactory.actorOf(propsOptions(Props(create(m))), n)
    }).getOrElse({
      actorRefFactory.actorOf(propsOptions(Props(create(m))))
    })
  }

}
