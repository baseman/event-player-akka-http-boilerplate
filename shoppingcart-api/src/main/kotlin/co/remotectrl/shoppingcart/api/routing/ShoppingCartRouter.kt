package co.remotectrl.shoppingcart.api.routing

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.Route
import akka.util.Timeout
import io.swagger.models.Info
import co.remotectrl.shoppingcart.api.spring.SpringExtension
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class ShoppingCartRouter(
        system: ActorSystem,
        springExtension: SpringExtension
) : AllDirectives() {

    val routeActor: ActorRef = system.actorOf(springExtension.props("shoppingCartActor"))
    val timeout = Timeout(Duration.create(5, TimeUnit.SECONDS))

    val cmdRouter = ShoppingCartCommandRouter(routeActor, timeout)
    val dtoRouter = ShoppingCartDtoRouter(routeActor, timeout)

    internal fun createRoute(): Route {
        return route(
                createSwaggerRoute(),
                pathPrefix("shoppingcart") {
                    route(
                            cmdRouter.commandRoutes(),
                            dtoRouter.dtoRoutes()
                    )
                }
        )
    }

    private val swaggerDocRouter: SwaggerDocRouter = SwaggerDocRouter(
            apiClasses = setOf(
                    ShoppingCartCommandRouter::class.java,
                    ShoppingCartDtoRouter::class.java
            ),
            apiInfo = Info()
                    .title("Shopping Cart API")
                    .description("Simple akka-http application")
                    .version("1.0")
    )

    private fun createSwaggerRoute(): Route? {
        return route(
                swaggerDocRouter.createRoute(),
                path("swagger") {
                    getFromResource("swagger/index.html")
                },
                getFromResourceDirectory("swagger")
        )
    }
}

