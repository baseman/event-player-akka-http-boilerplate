package my.artifact.myeventplayer.common.event

import co.remotectrl.eventplayer.*
import my.artifact.myeventplayer.common.aggregate.MyAggregate

data class MyChangedEvent(
        override val legend: EventLegend<MyAggregate>,
        val myChangeVal: String
) : PlayEvent<MyAggregate> {

    override fun applyChangesTo(model: MyAggregate, latestVersion: Int): MyAggregate {
        return MyAggregate(
                AggregateLegend(legend.aggregateId, latestVersion),
                myVal = myChangeVal)
    }

    constructor() : this(EventLegend(0, 0, 0), "")

}