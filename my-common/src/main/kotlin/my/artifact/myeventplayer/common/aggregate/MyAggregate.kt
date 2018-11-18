package my.artifact.myeventplayer.common.aggregate

import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateLegend

data class MyAggregate(override val legend: AggregateLegend<MyAggregate> = AggregateLegend(), val myVal: String = "") : Aggregate<MyAggregate>
