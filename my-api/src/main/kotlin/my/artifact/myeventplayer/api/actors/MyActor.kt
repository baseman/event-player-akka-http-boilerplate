package my.artifact.myeventplayer.api.actors

import akka.actor.AbstractActor
import akka.event.Logging
import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateId
import co.remotectrl.eventplayer.PlayCommand
import my.artifact.myeventplayer.api.services.MyService
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
@Scope("prototype")
class MyActor(private val myService: MyService) : AbstractActor() {

    private val log = Logging.getLogger(context.system(), this)

    class MyRepository {
        companion object {
            val items = mutableListOf<MyAggregate>()
        }
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(AggregateCommandMessages.ExecuteCommand::class.java) {

                    try {
                        myService.commit(
                                MyRepository.items,
                                it.command as PlayCommand<MyAggregate>
                        )

                        sender.tell(AggregateCommandMessages.ActionPerformed(commandErr = null), self)

                    } catch (e: Error){
                        sender.tell(AggregateCommandMessages.ActionPerformed(commandErr = e.message), self)
                    }

                }
                .match(AggregateCommandMessages.ExecuteCommandForAggregateId::class.java) {

                    val aggregate = myService.getAggregate(MyRepository.items, it.aggregateId as AggregateId<MyAggregate>)

                    when {
                        aggregate != null -> try {
                            myService.commit(
                                    items = MyRepository.items,
                                    aggregate = aggregate,
                                    cmd = it.command as PlayCommand<MyAggregate>
                            )

                            sender.tell(AggregateCommandMessages.ActionPerformedForAggregateId(
                                    isAggregateFound = true,
                                    commandErr = null
                            ), self)

                        } catch (e: Error){
                            sender.tell(AggregateCommandMessages.ActionPerformedForAggregateId(
                                    isAggregateFound = true,
                                    commandErr = e.message
                            ), self)
                        }
                        else -> sender.tell(AggregateCommandMessages.ActionPerformedForAggregateId(
                                isAggregateFound = false,
                                commandErr = null
                        ), self)
                    }

                }
                .match(AggregateDtoMessages.GetItems::class.java) { getMsgs ->

                    sender.let {
                        val items = myService.getAggregates(MyRepository.items)
                        it.tell(AggregateDtoMessages.ReturnItems(items = items), self)
                    }

                }
                .match(AggregateDtoMessages.GetItem::class.java) { getMsg ->

                    sender.let {
                        val item = myService.getAggregate(MyRepository.items, getMsg.aggregateId)
                        it.tell(AggregateDtoMessages.ReturnItem(item = item), self)
                    }

                }
                .matchAny {
                    log.info("received unknown message")
                }.build()
    }

}

interface AggregateDtoMessages{
    class GetItem<TAggregate : Aggregate<TAggregate>>(val aggregateId: AggregateId<TAggregate>)
    class GetItems
    class ReturnItem<TAggregate : Aggregate<TAggregate>>(val item: TAggregate?)
    class ReturnItems<TAggregate : Aggregate<TAggregate>>(val items: Array<TAggregate>)
}

interface AggregateCommandMessages{
    class ActionPerformedForAggregateId(val isAggregateFound: Boolean, val commandErr: String?) : Serializable
    class ExecuteCommand<TAggregate : Aggregate<TAggregate>, TCommand: PlayCommand<TAggregate>>(val command: TCommand)
    class ExecuteCommandForAggregateId<TAggregate : Aggregate<TAggregate>, TCommand: PlayCommand<TAggregate>>(val aggregateId: AggregateId<TAggregate>, val command: TCommand)
    class ActionPerformed(val commandErr: String?)
}