package my.artifact.myeventplayer.api

import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.LoggingAdapter
import akka.http.javadsl.ConnectHttp
import akka.http.javadsl.Http
import akka.http.javadsl.server.Route
import akka.stream.ActorMaterializer
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ApplicationServer(val system: ActorSystem, router: MyRouter) {

    val route: Route = router.createRoute()

    private val log: LoggingAdapter = Logging.getLogger(system, this)

    fun init(){
        val http = Http.get(system)
        val materializer = ActorMaterializer.create(system)

        val flow = route.flow(system, materializer)

        http.bindAndHandle(flow, ConnectHttp.toHost("localhost", 8080), materializer)

        log.info("Server online at http://localhost:8080/")


        //todo: idenitfy graceful shutdown
//        val binding =

        //    fun cleanup(serverBinding: ServerBinding): CompletionStage<Any>{
        //        return serverBinding.unbind() as CompletionStage<*>
        //    }

//        binding
//                .thenCompose { it.unbind() }
//                .thenAccept { system.terminate() }
    }

}
