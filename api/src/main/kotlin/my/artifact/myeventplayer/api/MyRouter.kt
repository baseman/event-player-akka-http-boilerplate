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
import co.remotectrl.eventplayer.AggregateId
import my.artifact.myeventplayer.api.actors.AggregateMessages
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.command.MyChangeCommand
import org.springframework.stereotype.Component
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

@Component
class MyRouter(system: ActorSystem, springExtension: SpringExtension) : AllDirectives() {

    private val myActor: ActorRef = system.actorOf(springExtension.props("myActor"))

    private val log: LoggingAdapter = Logging.getLogger(system, this)

    private var timeout = Timeout(Duration.create(5, TimeUnit.SECONDS)) // usually we'd obtain the timeout from the system's configuration

    internal fun createRoute(): Route {
        return route(pathPrefix("cmd") {
            route(
                    path<String>(PathMatchers.segment()) { aggregateId ->
                        route(
                                postCommand<MyAggregate, MyChangeCommand>(AggregateId(aggregateId.toInt())) //todo: can add additional routes here
                        )
                    }
            )
        })
    }

    //todo: see if reified types can be moved to class
    private inline fun <TAggregate: MyAggregate, reified TCommand: MyChangeCommand> postCommand(aggregateId: AggregateId<MyAggregate>): Route {
        return post {
            val deserializer = Jackson.unmarshaller(TCommand::class.java)

//            val type = object : TypeReference<TAggregate>() {}.type //todo: remove
//            val cmdType = object : TypeReference<TCommand>() {}.type //todo: remove

            entity(deserializer) { command ->
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
