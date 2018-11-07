package my.artifact.myeventplayer.common.aggregate

import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateLegend

data class MyAggregate(override val legend: AggregateLegend<MyAggregate>, val myVal: String) : Aggregate<MyAggregate>{
    companion object {
        const val mediaType = "vnd.my.artifact.myeventplayer.common.aggregate.MyAggregate.api.v1+json"
    }
}
