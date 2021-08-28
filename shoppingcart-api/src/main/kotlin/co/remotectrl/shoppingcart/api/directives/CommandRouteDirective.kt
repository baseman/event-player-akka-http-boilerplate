package co.remotectrl.shoppingcart.api.directives

import akka.actor.ActorRef
import akka.http.javadsl.model.StatusCode
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.ExceptionHandler
import akka.http.javadsl.server.Route
import akka.http.javadsl.server.directives.RouteDirectives
import akka.pattern.PatternsCS
import akka.util.Timeout
import co.remotectrl.ctrl.event.*
import co.remotectrl.shoppingcart.api.actors.RootCommandMessages
import co.remotectrl.shoppingcart.api.actors.RootDtoMessages
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class CommandRouteDirective<TRoot : CtrlRoot<TRoot>>(val routeActor: ActorRef, val timeout: Timeout) : RouteDirectives() {

    val commandUnmarshaller = CommandUnmarshallingDirective<TRoot>()

    val invalidInputHandler: ExceptionHandler = ExceptionHandler.newBuilder().match(IllegalArgumentException::class.java) { e ->
        complete(StatusCodes.BAD_REQUEST, e.message)
    }.build()

    inline fun <reified TCommand : CtrlCommand<TRoot>> commandExecute(
        noinline factory: (RootLegend<TRoot>) -> TRoot): Route = handleExceptions(invalidInputHandler) {
        commandUnmarshaller.commandEntity<TCommand> { command ->
            val created = askGetNewItem(factory = factory).thenCompose { root ->
                askPlayPersist(root = root as TRoot, command = command)
            }

            onSuccess<StatusCode>({ created }, this::complete)
        }
    }

    inline fun <reified TCommand : CtrlCommand<TRoot>> commandExecute(rootId: String): Route = handleExceptions(invalidInputHandler) {

        commandUnmarshaller.commandEntity<TCommand> { command ->

            val tryCmdCompleted = askGetItem(rootId).thenCompose<StatusCode> { root ->

                if (root == null) {
                    CompletableFuture.completedFuture(StatusCodes.NOT_FOUND)
                } else askPlayPersist(root = root, command = command)
            }

            onSuccess<StatusCode>({ tryCmdCompleted }, this::complete)
        }

    }

    inline fun <reified TEvent : CtrlEvent<TRoot>> playFor(root: TRoot, event: TEvent): TRoot {
        val active = CtrlMutable(root)

        event.applyTo(active)

        return active.root
    }

    inline fun <reified TCommand : CtrlCommand<TRoot>> askPlayPersist(root: TRoot, command: TCommand): CompletionStage<StatusCode> {
        val exe = command.executeOn(root)

        val exeStage = when (exe) {
            is CtrlExecution.Validated -> {
                PatternsCS.ask(
                        routeActor,
                        RootCommandMessages.Persist(root = playFor(root = root, event = exe.event)),
                        timeout
                ).thenApply { StatusCodes.OK }
            }
            is CtrlExecution.Invalidated -> {
                CompletableFuture.completedFuture(StatusCodes.BAD_REQUEST)
            }
        }

        return exeStage.handle { result, err ->
            when {
                err != null -> StatusCodes.INTERNAL_SERVER_ERROR
                else -> result
            }
        }
    }

    fun askGetNewItem(factory: (RootLegend<TRoot>) -> TRoot): CompletionStage<TRoot> {
        return PatternsCS.ask(
                routeActor,
                RootDtoMessages.GetNewId(),
                timeout
        ).thenApply { obj ->
            factory(
                    RootLegend(rootIdVal = (obj as RootDtoMessages.ReturnId).value, latestVersion = 0)
            )
        }
    }

    fun askGetItem(rootId: String): CompletionStage<TRoot?> {
        return PatternsCS.ask(
                routeActor,
                RootDtoMessages.GetItem(RootId<TRoot>(rootId)),
                timeout
        ).thenApply { obj ->
            (obj as RootDtoMessages.ReturnItem<TRoot>).item
        }
    }
}