package my.artifact.myeventplayer.api.routing

import akka.actor.ActorRef
import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import akka.pattern.PatternsCS
import akka.util.Timeout
import co.remotectrl.eventplayer.AggregateId
import io.swagger.annotations.*
import my.artifact.myeventplayer.api.actors.AggregateDtoMessages
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/my")
@Api(value = "my" /* ,authorizations = [Authorization(value = "sampleoauth", scopes = [])]*/)
@Produces("application/json")
class MyDtoRouter(private val dtoActor: ActorRef, val timeout: Timeout) : AllDirectives() {
    @GET
    @Path("/{aggregateId}")
    @Produces("application/json")
    @ApiOperation(value = "get item by id", code = 200, nickname = "execute")
    @ApiImplicitParams(value = [
        (ApiImplicitParam(name = "aggregateId", value = "id for which item to return", required = true, paramType = "path", dataType = "integer"))
    ])
    @ApiResponses(value = [
        (ApiResponse(code = 200, response = MyAggregate::class, message = "item results")),
        (ApiResponse(code = 404, message = "item not found"))
    ])
    fun getForIdRoute(): Route? {

        return path<String>(PathMatchers.segment()) { aggregateId ->
            //            get {
            val returnItem = PatternsCS.ask(
                    dtoActor,
                    AggregateDtoMessages.GetItem<MyAggregate>(AggregateId(aggregateId.toInt())),
                    timeout
            ).thenApply { obj -> obj as AggregateDtoMessages.ReturnItem<MyAggregate> }

            onSuccess<AggregateDtoMessages.ReturnItem<MyAggregate>>({ returnItem }, { result ->
                complete<AggregateDtoMessages.ReturnItem<MyAggregate>>(StatusCodes.OK, result, Jackson.marshaller())
            })

//            }
        }
    }

    @GET
    @Path("/")
    @Produces("application/json")
    @ApiOperation(value = "get all items", code = 200, nickname = "execute")
    @ApiResponses(value = [
        (ApiResponse(code = 200, response = Array<MyAggregate>::class, message = "item list results"))
    ])
    fun getRoute(): Route? {
        return pathEnd {
            //            get {
            val returnItems = PatternsCS.ask(
                    dtoActor,
                    AggregateDtoMessages.GetItems(),
                    timeout
            ).thenApply { obj -> obj as AggregateDtoMessages.ReturnItems<MyAggregate> }

            onSuccess<AggregateDtoMessages.ReturnItems<MyAggregate>>({ returnItems }, { result ->
                complete<AggregateDtoMessages.ReturnItems<MyAggregate>>(StatusCodes.OK, result, Jackson.marshaller())
            })
//            }
        }
    }

    fun createRoute(): Route {
        return route(
                getRoute(),
                getForIdRoute()
        )
    }
}