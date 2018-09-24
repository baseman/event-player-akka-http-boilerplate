package my.artifact.myeventplayer.api

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import akka.util.Timeout
import co.remotectrl.eventplayer.AggregateId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.command.MyChangeCommand
import org.springframework.stereotype.Component
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces

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

    private val swaggerDocRouter: SwaggerDocRouter = SwaggerDocRouter()
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
    @Path("/cmd")
    @Produces("application/json")
    @Operation(summary = "submit application commands", description = "submit commands to be executed on the server",
            responses = [
                (ApiResponse(responseCode = "200", description = "command successfully executed")),
                (ApiResponse(responseCode = "422", description = "invalid input"))
            ])
    internal fun createCommandRoute(): Route {
        return route(
                pathPrefix("cmd") {
                    route(
                            path<String>(PathMatchers.segment()) { aggregateId ->
                                route(
                                        commandRouter.createPostCommand<MyChangeCommand>(AggregateId(aggregateId.toInt())) //todo: can add additional routes here
                                )
                            }
                    )
                })
    }

}

