package my.artifact.myeventplayer.api.services

import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateId
import org.springframework.stereotype.Service

@Service
class MyService {

    fun commit(items: MutableList<Any>, aggregate: Aggregate<*>) {

        val itemIndex = items.indexOfFirst {
            (it as Aggregate<*>).legend.aggregateId.value == aggregate.legend.aggregateId.value
        }

        when (itemIndex) {
            -1 -> {
                items.add(aggregate)

                if(seed >= 1){
                    seed--
                }
            }
            else -> items[itemIndex] = aggregate
        }
    }


    var seed: Int = 0
    fun getId(items: MutableList<Any>): Int{
        seed++
        return items.size + seed
    }

    fun getAggregates(items: MutableList<Aggregate<*>>): Array<Aggregate<*>> {
        return items.toTypedArray()
    }

    fun getAggregate(items: MutableList<Aggregate<*>>, aggregateId: AggregateId<out Aggregate<*>>): Aggregate<*>? {
        return getAggregates(items).find { it.legend.aggregateId.value == aggregateId.value }
    }
}