package co.remotectrl.myevent.api

import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.LoggingAdapter
import akka.http.javadsl.ConnectHttp
import akka.http.javadsl.Http
import akka.http.javadsl.ServerBinding
import akka.http.javadsl.server.Route
import akka.stream.ActorMaterializer
import co.remotectrl.myevent.api.routing.MyRouter
import org.springframework.stereotype.Component

@Component
class ApplicationServer(val system: ActorSystem, router: MyRouter) {

    private val log: LoggingAdapter = Logging.getLogger(system, this)
    private lateinit var binding: ServerBinding

    val route = router.createRoute()

    fun bind() {

        val http = Http.get(system)
        val materializer = ActorMaterializer.create(system)
        val flow = route.flow(system, materializer)

        binding = http.bindAndHandle(flow, ConnectHttp.toHost("localhost", 8080), materializer).toCompletableFuture().get()

        this.log.info("Server online at http://localhost:8080/")
    }

    fun onShutdown(fShutdown: (binding: ServerBinding, system: ActorSystem) -> Unit) {
        fShutdown(binding, system)
    }

}
