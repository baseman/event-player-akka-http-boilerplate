package co.remotectrl.shoppingcart.api.routing

import akka.actor.ActorRef
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import akka.util.Timeout
import co.remotectrl.ctrl.event.RootLegend
import io.swagger.annotations.*
import co.remotectrl.shoppingcart.api.actors.RootCommandMessages
import co.remotectrl.shoppingcart.api.directives.CommandRouteDirective
import co.remotectrl.shoppingcart.common.root.ShoppingCartRoot
import co.remotectrl.shoppingcart.common.command.UpdateCommand
import co.remotectrl.shoppingcart.common.command.DiscountApply
import co.remotectrl.shoppingcart.common.command.AddCommand
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/shoppingcart")
@Api(value = "shoppingcart", authorizations = [
    Authorization(value = "sampleoauth", scopes = [])
])
@Produces("application/json")
class ShoppingCartCommandRouter(
        routeActor: ActorRef,
        timeout: Timeout
) : AllDirectives() {

    private val commandHandler: CommandRouteDirective<ShoppingCartRoot> = CommandRouteDirective(routeActor, timeout)

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
        AddCommand.mediaType
    ])
    @ApiOperation(value = "execute shopping cart commands", code = 200, nickname = "execute", response = RootCommandMessages.ActionPerformed::class)
    @ApiImplicitParams(value = [
        (ApiImplicitParam(name = "body", value = "command to execute", required = false, paramType = "body", dataType = "object"))
    ])
    @ApiResponses(value = [
        (ApiResponse(code = 200, response = RootCommandMessages.ActionPerformed::class, message = "command successfully executed")),
        (ApiResponse(code = 400, message = "invalid input"))
    ])
    internal fun commandRoute(): Route {
        return pathEnd {
            post {
                route(
                        commandHandler.commandExecute<AddCommand>(createRoot) //todo: can add additional routes here
                )
            }
        }
    }

    @POST
    @Path("/cmd/{rootId}")
    @Produces("application/json")
    @Consumes(value = [
        AddCommand.mediaType,
        UpdateCommand.mediaType,
        DiscountApply.mediaType
    ])
    @ApiOperation(value = "execute shopping cart commands", code = 200, nickname = "execute", response = RootCommandMessages.ActionPerformed::class)
    @ApiImplicitParams(value = [
        (ApiImplicitParam(name = "rootId", value = "id for which command belongs to", required = true, paramType = "path", dataType = "integer")),
        (ApiImplicitParam(name = "body", value = "command to execute", required = false, paramType = "body", dataType = "object"))
    ])
    @ApiResponses(value = [
        (ApiResponse(code = 200, response = RootCommandMessages.ActionPerformed::class, message = "command successfully executed")),
        (ApiResponse(code = 400, message = "invalid input")),
        (ApiResponse(code = 404, message = "shoppingcart item not found"))
    ])
    internal fun commandIdRoute(): Route {
        return route(
                path<String>(PathMatchers.segment()) { rootId ->
                    post {
                        route(
                                commandHandler.commandExecute<AddCommand>(rootId), //todo: can add additional routes here
                                commandHandler.commandExecute<UpdateCommand>(rootId), //todo: can add additional routes here
                                commandHandler.commandExecute<DiscountApply>(rootId)
                        )
                    }
                }
        )
    }

    companion object {
        private val createRoot: (RootLegend<ShoppingCartRoot>) -> ShoppingCartRoot = { it -> ShoppingCartRoot(it) }
    }

}