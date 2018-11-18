package my.artifact.myeventplayer.api.services

import co.remotectrl.eventplayer.*
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import org.springframework.stereotype.Service

@Service
class MyService {

    fun commit(items: MutableList<Any>, aggregate: Aggregate<*>) {

        val itemIndex = items.indexOfFirst {
            (it as Aggregate<*>).legend.aggregateId.value == aggregate.legend.aggregateId.value
        }

        when (itemIndex) {
            -1 -> items.add(aggregate)
            else -> items[itemIndex] = aggregate
        }
    }

    fun getAggregates(items: MutableList<Aggregate<*>>): Array<Aggregate<*>> {
        return items.toTypedArray()
    }

    fun getAggregate(items: MutableList<Aggregate<*>>, aggregateId: AggregateId<out Aggregate<*>>): Aggregate<*>? {
        return getAggregates(items).find { it.legend.aggregateId.value == aggregateId.value }
    }
}