package my.artifact.myeventplayer.api.routing

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.Route
import akka.util.Timeout
import io.swagger.models.Info
import my.artifact.myeventplayer.api.spring.SpringExtension
import org.springframework.stereotype.Component
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

@Component
class MyRouter(
        system: ActorSystem,
        springExtension: SpringExtension
) : AllDirectives() {

    val routeActor: ActorRef = system.actorOf(springExtension.props("myActor"))
    val timeout = Timeout(Duration.create(5, TimeUnit.SECONDS))

    val cmdRouter = MyCommandRouter(routeActor, timeout)
    val dtoRouter = MyDtoRouter(routeActor, timeout)


    internal fun createRoute(): Route {
        return route(
                createSwaggerRoute(),
                pathPrefix("my") {
                    route(
                            cmdRouter.createRoute(),
                            dtoRouter.createRoute()
                    )
                }
        )
    }

    private val swaggerDocRouter: SwaggerDocRouter = SwaggerDocRouter(
            apiClasses = setOf(
                    MyCommandRouter::class.java,
                    MyDtoRouter::class.java
            ),
            apiInfo = Info()
                    .title("My API")
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

