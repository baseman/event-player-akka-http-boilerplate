package my.artifact.myeventplayer.jvm.assert

import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateLegend
import co.remotectrl.eventplayer.EventLegend
import co.remotectrl.eventplayer.PlayEvent
import org.amshove.kluent.shouldEqual

class AssertUtil{
    companion object {

        fun <T : Aggregate<T>> assertEvent(actual: EventLegend<T>, expected: EventLegend<T>) {
            actual.eventId.value shouldEqual expected.eventId.value
            actual.aggregateId.value shouldEqual expected.aggregateId.value
            actual.version shouldEqual expected.version
        }

        fun <T : Aggregate<T>> assertAggregateEvent(model: AggregateLegend<T>, evt: PlayEvent<T>) {
            model.latestVersion shouldEqual evt.legend.version
        }
    }
}