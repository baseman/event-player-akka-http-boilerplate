package my.artifact.myeventplayer.api

import akka.actor.ActorRef
import akka.event.LoggingAdapter
import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.Route
import akka.pattern.PatternsCS
import akka.util.Timeout
import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateId
import co.remotectrl.eventplayer.PlayCommand
import my.artifact.myeventplayer.api.actors.AggregateMessages

class CommandRouter<TAggregate : Aggregate<TAggregate>>(val routeActor: ActorRef, val timeout: Timeout, val log: LoggingAdapter) : AllDirectives(){
    inline fun <reified TCommand: PlayCommand<TAggregate>> createPostCommand(aggregateId: AggregateId<TAggregate>): Route {
        return post {
            val deserializer = Jackson.unmarshaller(TCommand::class.java)

            entity(deserializer) { command ->
                val cmdPosted = PatternsCS.ask(routeActor, AggregateMessages.ExecuteCommand(aggregateId, command), timeout)
                        .thenApply { obj -> obj as AggregateMessages.ActionPerformed }

                onSuccess<AggregateMessages.ActionPerformed>({ cmdPosted },{ performed ->
                    log.info("command executed [{}]: {}", aggregateId, performed.description)
                    complete<AggregateMessages.ActionPerformed>(StatusCodes.OK, performed, Jackson.marshaller())
                })
            }
        }
    }
}