package co.remotectrl.myevent.api.actors

import akka.actor.AbstractActor
import akka.event.Logging
import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateId
import co.remotectrl.eventplayer.PlayCommand
import co.remotectrl.myevent.api.services.MyService
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class MyActor(private val myService: MyService) : AbstractActor() {

    private val log = Logging.getLogger(context.system(), this)

    class MyRepository {
        companion object {
            val items = mutableListOf<Any>()
        }
    }

    var seed: Int = 0

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(AggregateCommandMessages.Persist::class.java) {

                    try {
                        myService.commit(
                                MyRepository.items,
                                aggregate = it.aggregate
                        )

                        sender.tell(AggregateCommandMessages.ActionPerformed(), self)

                    } catch (e: Error) {
                        sender.tell(AggregateCommandMessages.ActionPerformed(), self)
                    }

                }
                .match(AggregateDtoMessages.GetNewId::class.java) {
                    sender.tell(AggregateDtoMessages.ReturnId(value = myService.getId(MyRepository.items)), self)
                }
                .match(AggregateDtoMessages.GetItems::class.java) { getMsgs ->

                    sender.let {
                        val items = myService.getAggregates(MyRepository.items as MutableList<Aggregate<*>>)
                        it.tell(AggregateDtoMessages.ReturnItems(items = items), self)
                    }

                }
                .match(AggregateDtoMessages.GetItem::class.java) { getMsg ->

                    sender.let {
                        val item = myService.getAggregate(MyRepository.items as MutableList<Aggregate<*>>, getMsg.aggregateId)
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
    class ReturnItem<TAggregate : Aggregate<*>>(val item: TAggregate?)
    class ReturnItems(val items: Array<Aggregate<*>>)
    class GetNewId
    class ReturnId(val value: Int)
}

interface AggregateCommandMessages{
    class Persist<TAggregate : Aggregate<TAggregate>>(val aggregate: TAggregate)
    class ActionPerformed
}