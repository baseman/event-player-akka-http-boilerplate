package my.artifact.myeventplayer.api

import akka.actor.*
import akka.http.javadsl.server.*
import org.springframework.stereotype.*

import akka.actor.ActorRef.*

@Component
class MyRouter(system: ActorSystem, springExtension: SpringExtension) : AllDirectives() {

    private val myActor: ActorRef = system.actorOf(springExtension.props("myActor"))

    //todo: define http routes

    internal fun createRoute(): Route {
        return route(
                path("/operation") {
                    post {
                        myActor.tell("Hello World", noSender())
                        complete("<h1>Hello World</h1>")
                    }
                })
    }
}
