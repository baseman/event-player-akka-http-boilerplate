package co.remotectrl.myevent.common.event

import co.remotectrl.eventplayer.*
import co.remotectrl.myevent.common.aggregate.MyAggregate

data class MyChangedEvent(
        override val legend: EventLegend<MyAggregate>,
        val myChangeVal: String
) : PlayEvent<MyAggregate> {

    override fun applyChangesTo(aggregate: MyAggregate, latestVersion: Int): MyAggregate {
        return MyAggregate(
                AggregateLegend(legend.aggregateId, latestVersion),
                myVal = myChangeVal)
    }

    constructor() : this(EventLegend(0, 0, 0), "")

}