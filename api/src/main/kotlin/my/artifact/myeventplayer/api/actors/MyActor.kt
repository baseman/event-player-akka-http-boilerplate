package my.artifact.myeventplayer.api.actors

import akka.actor.UntypedActor
import akka.event.Logging
import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateId
import co.remotectrl.eventplayer.PlayCommand
import my.artifact.myeventplayer.api.services.MyService
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
@Scope("prototype")
class MyActor(private val myService: MyService) : UntypedActor() {

    private val log = Logging.getLogger(context.system(), this)

    @Throws(Throwable::class)
    override fun onReceive(message: Any) {
        log.info("Service method result: {}", myService.something(22))
        log.info("Message received: {}", message.toString())
    }
}

interface AggregateMessages{
    class ActionPerformed(val description: String) : Serializable
    class ExecuteCommand<TAggregate : Aggregate<TAggregate>>(val aggregateId: AggregateId<TAggregate>, command: PlayCommand<TAggregate>)
}