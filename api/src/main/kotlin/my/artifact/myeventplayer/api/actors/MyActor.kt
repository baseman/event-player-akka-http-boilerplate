package my.artifact.myeventplayer.api.actors

import akka.actor.*
import akka.event.*
import my.artifact.myeventplayer.api.services.MyService
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class MyActor(private val myService: MyService) : UntypedActor() {
    private val log = Logging.getLogger(context().system(), this)

    @Throws(Throwable::class)
    override fun onReceive(message: Any) {
        log.info("Service method result: {}", myService.something(22))
        log.info("Message received: {}", message.toString())
    }
}
