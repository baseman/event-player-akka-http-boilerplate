package my.artifact.myeventplayer.api.directives

import akka.actor.ActorRef
import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.ExceptionHandler
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

    val invalidInputHandler: ExceptionHandler = ExceptionHandler.newBuilder()
            .match(IllegalArgumentException::class.java) { e ->
                complete(StatusCodes.BAD_REQUEST, e.message)
            }.build()

    inline fun <reified TCommand : PlayCommand<TAggregate>> commandExecute(): Route = handleExceptions(invalidInputHandler) {

        commandUnmarshaller.commandEntity<TCommand> { command ->
            val cmdPosted = PatternsCS.ask(
                    routeActor,
                    AggregateCommandMessages.ExecuteCommand(command = command),
                    timeout
            ).thenApply { obj ->
                obj as AggregateCommandMessages.ActionPerformed
            }

            onSuccess<AggregateCommandMessages.ActionPerformed>({ cmdPosted }, { performed ->

                val statusCode = when {
                    performed.commandErr != null -> StatusCodes.BAD_REQUEST
                    else -> StatusCodes.OK
                }

                complete(statusCode)

            })
        }
    }

    inline fun <reified TCommand : PlayCommand<TAggregate>> commandExecute(aggregateId: String): Route = handleExceptions(invalidInputHandler) {
        commandUnmarshaller.commandEntity<TCommand> { command ->
            val cmdPosted = PatternsCS.ask(
                    routeActor,
                    AggregateCommandMessages.ExecuteCommandForAggregateId(AggregateId(aggregateId.toInt()), command),
                    timeout
            ).thenApply { obj ->
                obj as AggregateCommandMessages.ActionPerformedForAggregateId
            }

            onSuccess<AggregateCommandMessages.ActionPerformedForAggregateId>({ cmdPosted }, { performed ->
                val statusCode = when {
                    !performed.isAggregateFound -> StatusCodes.NOT_FOUND
                    performed.commandErr != null -> StatusCodes.BAD_REQUEST
                    else -> StatusCodes.OK
                }

                complete(statusCode)
            })
        }
    }
}