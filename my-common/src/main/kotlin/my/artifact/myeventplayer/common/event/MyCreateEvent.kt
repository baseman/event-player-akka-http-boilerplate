package my.artifact.myeventplayer.common.event

import co.remotectrl.eventplayer.AggregateLegend
import co.remotectrl.eventplayer.EventLegend
import co.remotectrl.eventplayer.PlayEvent
import my.artifact.myeventplayer.common.aggregate.MyAggregate

data class MyCreatedEvent(
        override val legend: EventLegend<MyAggregate>,
        val myInitialVal: String
) : PlayEvent<MyAggregate> {
    override fun applyChangesTo(model: MyAggregate, latestVersion: Int): MyAggregate {
        return MyAggregate(
                AggregateLegend(legend.aggregateId, latestVersion),
                myVal = myInitialVal)
    }

    constructor() : this(EventLegend(0, 0, 0), "")
}