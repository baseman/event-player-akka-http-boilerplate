package co.remotectrl.myevent.api.services

import co.remotectrl.ctrl.event.AggregateId
import co.remotectrl.ctrl.event.CtrlAggregate

class MyService {

    fun commit(items: MutableList<Any>, aggregate: CtrlAggregate<*>) {

        val itemIndex = items.indexOfFirst {
            (it as CtrlAggregate<*>).legend.aggregateId.value == aggregate.legend.aggregateId.value
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
    fun getId(items: MutableList<Any>): String{
        seed++
        return (items.size + seed).toString()
    }

    fun getAggregates(items: MutableList<CtrlAggregate<*>>): Array<CtrlAggregate<*>> {
        return items.toTypedArray()
    }

    fun getAggregate(items: MutableList<CtrlAggregate<*>>, aggregateId: AggregateId<out CtrlAggregate<*>>): CtrlAggregate<*>? {
        return getAggregates(items).find { it.legend.aggregateId.value == aggregateId.value }
    }
}