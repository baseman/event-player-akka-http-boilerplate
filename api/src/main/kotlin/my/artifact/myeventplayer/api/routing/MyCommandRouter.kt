package my.artifact.myeventplayer.api.routing

import akka.actor.ActorRef
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import akka.util.Timeout
import io.swagger.annotations.*
import my.artifact.myeventplayer.api.actors.AggregateMessages
import my.artifact.myeventplayer.api.directives.CommandRouteDirective
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.command.MyChangeCommand
import org.springframework.stereotype.Component
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

    @POST
    @Path("/{aggregateId}/cmd")
    @Produces("application/json")
    @ApiOperation(value = "execute my commands", code = 200, nickname = "execute", response = AggregateMessages.ActionPerformed::class)
    @ApiImplicitParams(value = [
        (ApiImplicitParam(name = "aggregateId", value = "id for which command belongs to", required = true, paramType = "path", dataType = "integer")),
        (ApiImplicitParam(name = "body", value = "command to execute", required = false, paramType = "body", dataType = "object"))
    ])
    @ApiResponses(value = [
        (ApiResponse(code = 200, response = AggregateMessages.ActionPerformed::class, message = "command successfully executed")),
        (ApiResponse(code = 400, message = "invalid input")),
        (ApiResponse(code = 404, message = "my item not found"))
    ])
    internal fun createRoute(): Route {
        return pathPrefix("my") {
            path<String>(PathMatchers.segment().slash("cmd")) { aggregateId ->
                post {
                    route(
                            commandHandler.commandExecute<MyChangeCommand>(aggregateId) //todo: can add additional routes here
                    )
                }
            }
        }
    }

}