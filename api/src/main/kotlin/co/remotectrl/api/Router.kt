package co.remotectrl.api

import akka.actor.*
import akka.http.javadsl.server.*

import akka.actor.ActorRef.*
import co.remotectrl.api.actors.MyActor

class Router(system: ActorSystem) : AllDirectives() {

    private val myActor: ActorRef = system.actorOf(Props.create(MyActor::class.java))

    internal fun createRoute(): Route {
        return route(
                path("hello") {
                    get {

                        //todo: define http routes

                        myActor.tell("Hello World", noSender())
                        complete("<h1>Hello World</h1>")
                    }
                })
    }
}
