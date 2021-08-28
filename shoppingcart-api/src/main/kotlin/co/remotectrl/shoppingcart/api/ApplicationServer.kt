package co.remotectrl.shoppingcart.api

import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.LoggingAdapter
import akka.http.javadsl.ConnectHttp
import akka.http.javadsl.Http
import akka.http.javadsl.ServerBinding
import akka.stream.ActorMaterializer
import co.remotectrl.shoppingcart.api.routing.ShoppingCartRouter

class ApplicationServer(val system: ActorSystem, router: ShoppingCartRouter) {

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
