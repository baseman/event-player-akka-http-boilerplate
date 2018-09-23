package my.artifact.myeventplayer.api.actors

import akka.actor.AbstractActor
import akka.event.Logging
import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateId
import co.remotectrl.eventplayer.PlayCommand
import my.artifact.myeventplayer.api.services.MyService
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.command.MyChangeCommand
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
@Scope("prototype")
class MyActor(private val myService: MyService) : AbstractActor() {

    private val log = Logging.getLogger(context.system(), this)

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(AggregateMessages.ExecuteCommand::class.java) {
                    myService.something(it.aggregateId, it.command)
                    sender.tell(
                            AggregateMessages.ActionPerformed(
                                    "ok"
                            ), self)
                }
                .matchAny {
                    log.info("received unknown message")
                }.build()
    }

}

interface AggregateMessages{
    class ActionPerformed(val description: String) : Serializable
    class ExecuteCommand<TAggregate : Aggregate<TAggregate>, TCommand: PlayCommand<TAggregate>>(val aggregateId: AggregateId<TAggregate>, val command: TCommand)
}