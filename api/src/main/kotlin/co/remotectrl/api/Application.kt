package co.remotectrl.api

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.javadsl.ConnectHttp
import akka.http.javadsl.Http
import akka.http.javadsl.ServerBinding
import akka.stream.ActorMaterializer
import java.util.concurrent.CompletionStage

class MyApiApplication {

    private val system = ActorSystem.create("my-system")!!
    private val log = Logging.getLogger(system, this)!!

    fun run() {
        log.info("Your application started")

        val http = Http.get(system)
        val materializer = ActorMaterializer.create(system)

        val router = Router(system)
        val flow = router.createRoute().flow(system, materializer)
        val binding = http.bindAndHandle(flow, ConnectHttp.toHost("0.0.0.0", 8080), materializer)

        log.info("Server online at http://localhost:8080/\nPress RETURN to stop...")
        System.`in`.read()

        binding //todo: fix
                .thenCompose(Function<ServerBinding, CompletionStage<Any>> { ServerBinding.unbind() })
                .thenAccept({ unbound -> system.terminate() })
    }
}

fun main(args: Array<String>) {
    try {
        MyApiApplication().run()
    }
    catch (e: Exception) {
        System.out.println("err: $e")
        throw e
    }
}