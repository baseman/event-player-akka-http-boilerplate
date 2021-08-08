package co.remotectrl.myevent.api.routing

import akka.actor.ActorRef
import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import akka.pattern.PatternsCS
import akka.util.Timeout
import co.remotectrl.ctrl.event.RootId
import co.remotectrl.ctrl.event.CtrlRoot
import io.swagger.annotations.*
import co.remotectrl.myevent.api.actors.RootDtoMessages
import co.remotectrl.myevent.common.root.MyRoot
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/my")
@Api(value = "my" /* ,authorizations = [Authorization(value = "sampleoauth", scopes = [])]*/)
@Produces("application/json")
class MyDtoRouter(private val dtoActor: ActorRef, val timeout: Timeout) : AllDirectives() {
    @GET
    @Path("/item/{rootId}")
    @Produces("application/json")
    @ApiOperation(value = "get item by id", code = 200, nickname = "execute")
    @ApiImplicitParams(value = [
        (ApiImplicitParam(name = "rootId", value = "id for which item to return", required = true, paramType = "path", dataType = "integer"))
    ])
    @ApiResponses(value = [
        (ApiResponse(code = 200, response = MyRoot::class, message = "item results")),
        (ApiResponse(code = 404, message = "item not found"))
    ])
    fun getForIdRoute(): Route? {

        return rejectEmptyResponse {
        pathPrefix("item") {

                path<String>(PathMatchers.segment()) { rootId ->
                    //            get {
                    val returnItem = PatternsCS.ask(
                            dtoActor,
                            RootDtoMessages.GetItem<MyRoot>(RootId(rootId)),
                            timeout
                    ).thenApply { obj -> obj as RootDtoMessages.ReturnItem<MyRoot> }

                    onSuccess<RootDtoMessages.ReturnItem<MyRoot>>({ returnItem }, { result ->
                        when {
                            result.item == null -> complete("")
                            else -> complete<MyRoot>(StatusCodes.OK, result.item, Jackson.marshaller())
                        }
                    })

//            }
                }

            }
        }
    }

    @GET
    @Path("/items")
    @Produces("application/json")
    @ApiOperation(value = "get all items", code = 200, nickname = "execute")
    @ApiResponses(value = [
        (ApiResponse(code = 200, response = Array<MyRoot>::class, message = "item list results"))
    ])
    fun getRoute(): Route? {
        return pathPrefix("items") {
            get {
                val returnItems = PatternsCS.ask(
                        dtoActor,
                        RootDtoMessages.GetItems(),
                        timeout
                ).thenApply { obj -> obj as RootDtoMessages.ReturnItems }

                onSuccess<RootDtoMessages.ReturnItems>({ returnItems }, { result ->
                    complete<Array<CtrlRoot<*>>>(StatusCodes.OK, result.items, Jackson.marshaller())
                })
            }
        }
    }

    fun dtoRoutes(): Route {
        return route(
                getRoute(),
                getForIdRoute()
        )
    }
}