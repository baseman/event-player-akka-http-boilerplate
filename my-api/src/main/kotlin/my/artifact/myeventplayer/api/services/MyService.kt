package my.artifact.myeventplayer.api.services

import co.remotectrl.eventplayer.*
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import org.springframework.stereotype.Service

@Service
class MyService {

    fun commit(items: MutableList<MyAggregate>, cmd: PlayCommand<MyAggregate>) {

        val index = items.size + 1

        items.add(
                getMutable(
                        aggregate = MyAggregate(legend = AggregateLegend(index, 0), myVal = ""),
                        cmd = cmd
                )
        )
    }

    fun commit(items: MutableList<MyAggregate>, aggregate: MyAggregate, cmd: PlayCommand<MyAggregate>) {

        val itemIndex = items.indexOfFirst {
            it.legend.aggregateId.value == aggregate.legend.aggregateId.value
        }

        items[itemIndex] = getMutable(
                aggregate = aggregate,
                cmd = cmd
        )
    }

    private fun getMutable(aggregate: MyAggregate, cmd: PlayCommand<MyAggregate>): MyAggregate {
        val evt = cmd.executeOn(aggregate)
        val mutableAggregate = MutableAggregate(aggregate)
        evt.applyTo(mutableAggregate)
        return mutableAggregate.model
    }

    fun getAggregates(items: MutableList<MyAggregate>): Array<MyAggregate> {
        return items.toTypedArray()
    }

    fun getAggregate(items: MutableList<MyAggregate>, aggregateId: AggregateId<out Aggregate<*>>): MyAggregate? {
        return getAggregates(items).find { it.legend.aggregateId.value == aggregateId.value }
    }

}
