package my.artifact.myeventplayer.jvm.assert

import co.remotectrl.eventplayer.*
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldEqual

class AssertUtil{
    companion object {

        fun <TAggregate : Aggregate<TAggregate>>  assertExecution(
                actualExecution: PlayExecution<TAggregate, PlayEvent<TAggregate>, PlayInvalidation<TAggregate>>,
                expectedExecution: PlayExecution.Validated<TAggregate, PlayEvent<TAggregate>>
        ){
            actualExecution `should be instance of` PlayExecution.Validated::class.java

            val actualValid = (actualExecution as co.remotectrl.eventplayer.PlayExecution.Validated)
            assertEventLegend(actualValid.event.legend, expectedExecution.event.legend)
        }

        fun <TAggregate : Aggregate<TAggregate>> assertExecution(
                actualExecution: PlayExecution<TAggregate, PlayEvent<TAggregate>, PlayInvalidation<TAggregate>>,
                expectedExecution: PlayExecution.Invalidated<TAggregate>
        ) {
            actualExecution `should be instance of` PlayExecution.Invalidated::class.java

            val actualInvalid = (actualExecution as co.remotectrl.eventplayer.PlayExecution.Invalidated)
            actualInvalid.items.size shouldEqual expectedExecution.items.size
            actualInvalid.items[0].description shouldEqual expectedExecution.items[0].description
        }

        private fun <TAggregate : Aggregate<TAggregate>> assertEventLegend(actual: EventLegend<TAggregate>, expected: EventLegend<TAggregate>) {
            actual.eventId.value shouldEqual expected.eventId.value
            actual.aggregateId.value shouldEqual expected.aggregateId.value
            actual.version shouldEqual expected.version
        }

        fun <TAggregate : Aggregate<TAggregate>> assertAggregateEvent(model: AggregateLegend<TAggregate>, evt: PlayEvent<TAggregate>) {
            model.latestVersion shouldEqual evt.legend.version
        }
    }
}