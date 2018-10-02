package my.artifact.myeventplayer.api

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import akka.util.Timeout
import co.remotectrl.eventplayer.AggregateId
import io.swagger.annotations.*
import my.artifact.myeventplayer.api.actors.AggregateMessages
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.command.MyChangeCommand
import org.springframework.stereotype.Component
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces


@Path("/")
@Api(value = "my", authorizations = [
    Authorization(value="sampleoauth", scopes = [])
])
@Produces("application/json")
@Component
class MyRouter(system: ActorSystem, springExtension: SpringExtension) : AllDirectives() {

    private val commandRouter: CommandRouter<MyAggregate> = CommandRouter(
            routeActor = system.actorOf(springExtension.props("myActor")),
            timeout = Timeout(Duration.create(5, TimeUnit.SECONDS)),
            log = Logging.getLogger(system, this)
    )

    internal fun createRoute(): Route {
        return route(
                createSwaggerRoute(),
                createCommandRoute()
        )
    }

    private val swaggerDocRouter: SwaggerDocRouter = SwaggerDocRouter(setOf(MyRouter::class.java))
    private fun createSwaggerRoute(): Route? {
        return route(
                swaggerDocRouter.createRoute(),
                path("swagger") {
                    getFromResource("swagger/index.html")
                },
                getFromResourceDirectory("swagger")
        )
    }

    @POST
    @Path("/{aggregateId}/cmd")
    @Produces("application/json")
    @ApiOperation(value = "execute my commands", code = 200, nickname = "execute", response = AggregateMessages.ActionPerformed::class)
    @ApiImplicitParams(value = [
        (ApiImplicitParam(name = "aggregateId", value = "id for which command belongs to", required = true, paramType = "path", dataType = "integer")),
        (ApiImplicitParam(name = "body", value = "command to execute", required = false, paramType = "body", dataTypeClass = MyChangeCommand::class))
    ])
    @ApiResponses(value = [
        (ApiResponse(code = 200, response = AggregateMessages.ActionPerformed::class, message = "command successfully executed")),
        (ApiResponse(code = 400, message = "invalid input")),
        (ApiResponse(code = 404, message = "my item not found"))
    ])
    internal fun createCommandRoute(): Route {
        return pathPrefix("my") {

            path<String>(PathMatchers.segment().slash("cmd")) { aggregateId ->
                commandRouter.createPostCommand<MyChangeCommand>(AggregateId(aggregateId.toInt())) //todo: can add additional routes here
            }
        }
    }

}

