package co.remotectrl.api.actors

import akka.actor.*
import akka.event.*
import co.remotectrl.api.services.MyService

class MyActor(private val myService: MyService) : UntypedActor() {
    private val log = Logging.getLogger(context().system(), this)

    @Throws(Throwable::class)
    override fun onReceive(message: Any) {
        log.info("Service method result: {}", myService.something(22))
        log.info("Message received: {}", message.toString())
    }
}
