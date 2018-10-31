package my.artifact.myeventplayer.api.directives

import akka.actor.ActorRef
import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.Route
import akka.http.javadsl.server.directives.RouteDirectives
import akka.pattern.PatternsCS
import akka.util.Timeout
import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateId
import co.remotectrl.eventplayer.PlayCommand
import my.artifact.myeventplayer.api.actors.AggregateCommandMessages

class CommandRouteDirective<TAggregate : Aggregate<TAggregate>>(val routeActor: ActorRef, val timeout: Timeout) : RouteDirectives(){

    val commandUnmarshaller = CommandUnmarshallingDirective<TAggregate>()
    inline fun <reified TCommand : PlayCommand<TAggregate>> commandExecute(aggregateId: String): Route = commandUnmarshaller.commandEntity<TCommand> { command ->
        val cmdPosted = PatternsCS.ask(
                routeActor,
                AggregateCommandMessages.ExecuteCommand(AggregateId(aggregateId.toInt()), command),
                timeout
        ).thenApply { obj -> obj as AggregateCommandMessages.ActionPerformed }

        onSuccess<AggregateCommandMessages.ActionPerformed>({ cmdPosted }, { performed ->
            complete<AggregateCommandMessages.ActionPerformed>(StatusCodes.OK, performed, Jackson.marshaller())
        })
    }
}