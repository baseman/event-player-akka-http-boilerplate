package co.remotectrl.myevent.common.event

import co.remotectrl.ctrl.event.AggregateLegend
import co.remotectrl.ctrl.event.CtrlEvent
import co.remotectrl.ctrl.event.EventLegend
import co.remotectrl.myevent.common.aggregate.MyAggregate

data class MyChangedEvent(
        override val legend: EventLegend<MyAggregate>,
        val myChangeVal: String
) : CtrlEvent<MyAggregate> {

    override fun applyChangesTo(aggregate: MyAggregate, latestVersion: Int): MyAggregate {
        return MyAggregate(
                AggregateLegend(legend.aggregateId, latestVersion),
                myVal = myChangeVal)
    }

    constructor() : this(EventLegend("", "", 0), "")

}