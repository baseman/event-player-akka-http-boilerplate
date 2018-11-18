package my.artifact.myeventplayer.api.directives

import akka.actor.ActorRef
import akka.http.javadsl.model.StatusCode
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.ExceptionHandler
import akka.http.javadsl.server.Route
import akka.http.javadsl.server.directives.RouteDirectives
import akka.pattern.PatternsCS
import akka.util.Timeout
import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateId
import co.remotectrl.eventplayer.MutableAggregate
import co.remotectrl.eventplayer.PlayCommand
import my.artifact.myeventplayer.api.actors.AggregateCommandMessages
import my.artifact.myeventplayer.api.actors.AggregateDtoMessages
import scala.reflect.internal.Trees
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class CommandRouteDirective<TAggregate : Aggregate<TAggregate>>(val routeActor: ActorRef, val timeout: Timeout) : RouteDirectives() {

    val commandUnmarshaller = CommandUnmarshallingDirective<TAggregate>()

    val invalidInputHandler: ExceptionHandler = ExceptionHandler.newBuilder().match(IllegalArgumentException::class.java) { e ->
                complete(StatusCodes.BAD_REQUEST, e.message)
            }.build()

    inline fun <reified TCommand : PlayCommand<TAggregate>> commandExecute(aggregate: TAggregate): Route = handleExceptions(invalidInputHandler) {
        commandUnmarshaller.commandEntity<TCommand> { command ->
            onSuccess(askPlay(aggregate = aggregate, command = command), this::complete)
        }
    }

    inline fun <reified TCommand : PlayCommand<TAggregate>> commandExecute(aggregateId: String): Route = handleExceptions(invalidInputHandler) {

        commandUnmarshaller.commandEntity<TCommand> { command ->

            val tryCmdCompleted = askGetItem(aggregateId).thenCompose<StatusCode> { aggregate ->

                if (aggregate == null) {
                    CompletableFuture.completedFuture(StatusCodes.NOT_FOUND)
                }
                else askPlay(aggregate = aggregate, command = command)
            }

            onSuccess<StatusCode>({ tryCmdCompleted }, this::complete)
        }

    }

    inline fun <reified TCommand : PlayCommand<TAggregate>> playFor(aggregate: TAggregate, command: TCommand): TAggregate {
        val evt = command.executeOn(aggregate)
        val mutableAggregate = MutableAggregate(aggregate)
        evt.applyTo(mutableAggregate)
        return mutableAggregate.model
    }

    inline fun <reified TCommand : PlayCommand<TAggregate>> askPlay(aggregate: TAggregate, command: TCommand): CompletionStage<StatusCode> {
        return CompletableFuture.completedFuture(
                playFor(aggregate = aggregate, command = command)
        ).thenCompose { playedAggregate ->
            PatternsCS.ask(
                    routeActor,
                    AggregateCommandMessages.Persist(aggregate = playedAggregate),
                    timeout
            )
        }.handle { result, err ->
            when {
                result is AggregateCommandMessages.ActionPerformed -> StatusCodes.OK
                err.cause ?: err is IllegalStateException -> StatusCodes.BAD_REQUEST //todo: log err
                else -> StatusCodes.INTERNAL_SERVER_ERROR //todo: log err and result
            }
        }
    }

    fun askGetItem(aggregateId: String): CompletionStage<TAggregate?> {
        return PatternsCS.ask(
                routeActor,
                AggregateDtoMessages.GetItem(AggregateId<TAggregate>(aggregateId.toInt())),
                timeout
        ).thenApply { obj -> (obj as AggregateDtoMessages.ReturnItem<TAggregate>).item }
    }
}