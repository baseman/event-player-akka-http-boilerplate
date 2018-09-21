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
import org.springframework.stereotype.Component
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

@Component
class MyRouter<TAggregate: Aggregate<TAggregate>>(system: ActorSystem, springExtension: SpringExtension) : AllDirectives() {

    private val myActor: ActorRef = system.actorOf(springExtension.props("myActor"))

    private val log: LoggingAdapter = Logging.getLogger(system, this)

    private var timeout = Timeout(Duration.create(5, TimeUnit.SECONDS)) // usually we'd obtain the timeout from the system's configuration

    internal fun createRoute(): Route {
        return route(pathPrefix("cmd") {
            route(
                    path<String>(PathMatchers.segment()) { aggregateId ->
                        route(postCommand<PlayCommand<TAggregate>>(AggregateId(aggregateId.toInt())))
                    }
            )
        })
    }

    private inline fun <reified TCommand: PlayCommand<TAggregate>> postCommand(aggregateId: AggregateId<TAggregate>): Route {
        return post {
            entity<TCommand>(Jackson.unmarshaller(TCommand::class.java)) { command ->
                val cmdPosted = PatternsCS
                        .ask(myActor, AggregateMessages.ExecuteCommand(aggregateId, command), timeout)
                        .thenApply { obj -> obj as AggregateMessages.ActionPerformed }

                onSuccess<AggregateMessages.ActionPerformed>({ cmdPosted },{ performed ->
                    log.info("command executed [{}]: {}", aggregateId, performed.description)
                    complete<AggregateMessages.ActionPerformed>(StatusCodes.OK, performed, Jackson.marshaller())
                })
            }
        }
    }
}
