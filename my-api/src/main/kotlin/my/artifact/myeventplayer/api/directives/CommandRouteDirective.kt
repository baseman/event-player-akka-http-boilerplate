package my.artifact.myeventplayer.api.directives

import akka.actor.ActorRef
import akka.http.javadsl.model.StatusCode
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.ExceptionHandler
import akka.http.javadsl.server.Route
import akka.http.javadsl.server.directives.RouteDirectives
import akka.pattern.PatternsCS
import akka.util.Timeout
import co.remotectrl.eventplayer.*
import my.artifact.myeventplayer.api.actors.AggregateCommandMessages
import my.artifact.myeventplayer.api.actors.AggregateDtoMessages
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class CommandRouteDirective<TAggregate : Aggregate<TAggregate>>(val routeActor: ActorRef, val timeout: Timeout) : RouteDirectives() {

    val commandUnmarshaller = CommandUnmarshallingDirective<TAggregate>()

    val invalidInputHandler: ExceptionHandler = ExceptionHandler.newBuilder().match(IllegalArgumentException::class.java) { e ->
                complete(StatusCodes.BAD_REQUEST, e.message)
            }.build()

    inline fun <reified TCommand : PlayCommand<TAggregate>> commandExecute(noinline factory: (AggregateLegend<TAggregate>) -> TAggregate): Route = handleExceptions(invalidInputHandler) {
        commandUnmarshaller.commandEntity<TCommand> { command ->
            val created = askGetNewItem(factory = factory).thenCompose { aggregate ->
                askPlayPersist(aggregate = aggregate as TAggregate, command = command)
            }

            onSuccess<StatusCode>({ created }, this::complete)
        }
    }

    inline fun <reified TCommand : PlayCommand<TAggregate>> commandExecute(aggregateId: String): Route = handleExceptions(invalidInputHandler) {

        commandUnmarshaller.commandEntity<TCommand> { command ->

            val tryCmdCompleted = askGetItem(aggregateId).thenCompose<StatusCode> { aggregate ->

                if (aggregate == null) {
                    CompletableFuture.completedFuture(StatusCodes.NOT_FOUND)
                }
                else askPlayPersist(aggregate = aggregate, command = command)
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

    inline fun <reified TCommand : PlayCommand<TAggregate>> askPlayPersist(aggregate: TAggregate, command: TCommand): CompletionStage<StatusCode> {
        return CompletableFuture.completedFuture(0).thenApply {
            playFor(aggregate = aggregate, command = command)
        }.thenCompose { playedAggregate ->
            PatternsCS.ask(
                    routeActor,
                    AggregateCommandMessages.Persist(aggregate = playedAggregate),
                    timeout
            )
        }.handle { result, err ->
            val statusCode = when {
                result is AggregateCommandMessages.ActionPerformed -> StatusCodes.OK
                err.cause ?: err is IllegalStateException -> StatusCodes.BAD_REQUEST //todo: log err
                else -> StatusCodes.INTERNAL_SERVER_ERROR //todo: log err and result
            }
            statusCode
        }
    }

    fun askGetNewItem(factory: (AggregateLegend<TAggregate>) -> TAggregate): CompletionStage<TAggregate> {
        return PatternsCS.ask(
                routeActor,
                AggregateDtoMessages.GetNewId(),
                timeout
        ).thenApply { obj ->
            factory(
                    AggregateLegend(aggregateIdVal = (obj as AggregateDtoMessages.ReturnId).value, latestVersion = 0)
            )
        }
    }

    fun askGetItem(aggregateId: String): CompletionStage<TAggregate?> {
        return PatternsCS.ask(
                routeActor,
                AggregateDtoMessages.GetItem(AggregateId<TAggregate>(aggregateId.toInt())),
                timeout
        ).thenApply { obj ->
            (obj as AggregateDtoMessages.ReturnItem<TAggregate>).item
        }
    }
}