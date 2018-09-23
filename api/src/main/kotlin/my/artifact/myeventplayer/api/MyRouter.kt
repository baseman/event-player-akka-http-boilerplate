package my.artifact.myeventplayer.api

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.LoggingAdapter
import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import akka.pattern.PatternsCS
import akka.util.Timeout
import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateId
import co.remotectrl.eventplayer.PlayCommand
import my.artifact.myeventplayer.api.actors.AggregateMessages
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.command.MyChangeCommand
import org.springframework.stereotype.Component
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

@Component
class MyRouter(system: ActorSystem, springExtension: SpringExtension) : AllDirectives() {

    private val commandRouteFactory: CommandRouteFactory<MyAggregate> = CommandRouteFactory(
            routeActor = system.actorOf(springExtension.props("myActor")),
            timeout = Timeout(Duration.create(5, TimeUnit.SECONDS)),
            log = Logging.getLogger(system, this)
    )

    internal fun createRoute(): Route {
        return route(pathPrefix("cmd") {
            route(
                    path<String>(PathMatchers.segment()) { aggregateId ->
                        route(
                                commandRouteFactory.postCommand<MyChangeCommand>(AggregateId(aggregateId.toInt())) //todo: can add additional routes here
                        )
                    }
            )
        })
    }

}

class CommandRouteFactory<TAggregate : Aggregate<TAggregate>>(val routeActor: ActorRef, val timeout: Timeout, val log: LoggingAdapter ) : AllDirectives(){
    inline fun <reified TCommand: PlayCommand<TAggregate>> postCommand(aggregateId: AggregateId<TAggregate>): Route {
        return post {
            val deserializer = Jackson.unmarshaller(TCommand::class.java)

            entity(deserializer) { command ->
                val cmdPosted = PatternsCS
                        .ask(routeActor, AggregateMessages.ExecuteCommand(aggregateId, command), timeout)
                        .thenApply { obj -> obj as AggregateMessages.ActionPerformed }

                onSuccess<AggregateMessages.ActionPerformed>({ cmdPosted },{ performed ->
                    log.info("command executed [{}]: {}", aggregateId, performed.description)
                    complete<AggregateMessages.ActionPerformed>(StatusCodes.OK, performed, Jackson.marshaller())
                })
            }
        }
    }
}
