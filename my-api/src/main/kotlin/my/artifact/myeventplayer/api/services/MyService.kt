package my.artifact.myeventplayer.api.services

import co.remotectrl.eventplayer.*
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import org.springframework.stereotype.Service

@Service
class MyService {

    fun commit(items: MutableList<MyAggregate>, aggregateId: AggregateId<MyAggregate>, cmd: PlayCommand<MyAggregate>) {

        val itemIndex = items.indexOfFirst {
            it.legend.aggregateId.value == aggregateId.value
        }

        val index = if (itemIndex == -1) {
            items.add(MyAggregate(legend = AggregateLegend(aggregateId, 0), myVal = ""))
            items.size - 1
        }
        else {
            itemIndex
        }

        val aggregate = items[index]

        val evt = cmd.executeOn(aggregate)
        val mutableAggregate = MutableAggregate(aggregate)
        evt.applyTo(mutableAggregate)

        items[index] = mutableAggregate.model
    }

    fun getAggregates(items: MutableList<MyAggregate>): Array<MyAggregate> {
        return items.toTypedArray()
    }

    fun getAggregate(items: MutableList<MyAggregate>, aggregateId: AggregateId<out Aggregate<*>>): MyAggregate? {
        return getAggregates(items).find { it.legend.aggregateId.value == aggregateId.value }
    }

}
