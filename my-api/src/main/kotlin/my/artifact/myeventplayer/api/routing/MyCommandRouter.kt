package my.artifact.myeventplayer.api.routing

import akka.actor.ActorRef
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import akka.util.Timeout
import io.swagger.annotations.*
import my.artifact.myeventplayer.api.actors.AggregateCommandMessages
import my.artifact.myeventplayer.api.directives.CommandRouteDirective
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.command.MyChangeCommand
import my.artifact.myeventplayer.common.command.MyCreateCommand
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/my")
@Api(value = "my", authorizations = [
    Authorization(value = "sampleoauth", scopes = [])
])
@Produces("application/json")
class MyCommandRouter(
        routeActor: ActorRef,
        timeout: Timeout
) : AllDirectives() {

    private val commandHandler: CommandRouteDirective<MyAggregate> = CommandRouteDirective(routeActor, timeout)

    internal fun commandRoutes(): Route {
        return pathPrefix("cmd") {
            route(
                    commandRoute(),
                    commandIdRoute()
            )
        }
    }

    @POST
    @Path("/cmd")
    @Produces("application/json")
    @Consumes(value = [
        MyCreateCommand.mediaType
    ])
    @ApiOperation(value = "execute my commands", code = 200, nickname = "execute", response = AggregateCommandMessages.ActionPerformedForAggregateId::class)
    @ApiImplicitParams(value = [
        (ApiImplicitParam(name = "body", value = "command to execute", required = false, paramType = "body", dataType = "object"))
    ])
    @ApiResponses(value = [
        (ApiResponse(code = 200, response = AggregateCommandMessages.ActionPerformedForAggregateId::class, message = "command successfully executed")),
        (ApiResponse(code = 400, message = "invalid input"))
    ])
    internal fun commandRoute(): Route {
        return pathEnd{
            post {
                route(
                        commandHandler.commandExecute<MyCreateCommand>() //todo: can add additional routes here
                )
            }
        }
    }

    @POST
    @Path("/cmd/{aggregateId}")
    @Produces("application/json")
    @Consumes(value = [
        MyChangeCommand.mediaType
    ])
    @ApiOperation(value = "execute my commands", code = 200, nickname = "execute", response = AggregateCommandMessages.ActionPerformedForAggregateId::class)
    @ApiImplicitParams(value = [
        (ApiImplicitParam(name = "aggregateId", value = "id for which command belongs to", required = true, paramType = "path", dataType = "integer")),
        (ApiImplicitParam(name = "body", value = "command to execute", required = false, paramType = "body", dataType = "object"))
    ])
    @ApiResponses(value = [
        (ApiResponse(code = 200, response = AggregateCommandMessages.ActionPerformedForAggregateId::class, message = "command successfully executed")),
        (ApiResponse(code = 400, message = "invalid input")),
        (ApiResponse(code = 404, message = "my item not found"))
    ])
    internal fun commandIdRoute(): Route {
        return rejectEmptyResponse {
            path<String>(PathMatchers.segment()) { aggregateId ->
                post {
                    route(
                            commandHandler.commandExecute<MyChangeCommand>(aggregateId) //todo: can add additional routes here
                    )
                }
            }
        }
    }

}