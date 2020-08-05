package co.remotectrl.myevent.assert

import co.remotectrl.ctrl.event.AggregateLegend
import co.remotectrl.ctrl.event.CtrlAggregate
import co.remotectrl.ctrl.event.CtrlEvent
import co.remotectrl.ctrl.event.CtrlExecution
import co.remotectrl.ctrl.event.CtrlInvalidation
import co.remotectrl.ctrl.event.EventLegend

class AssertUtil {
    companion object {

        fun <TAggregate : CtrlAggregate<TAggregate>> assertExecution(
            actual: CtrlExecution<TAggregate, CtrlEvent<TAggregate>, CtrlInvalidation>,
            expected: CtrlExecution.Validated<TAggregate, CtrlEvent<TAggregate>>
        ) {
            val actualValid = (actual as CtrlExecution.Validated)
            assertEventType(actual.event, expected.event)
            assertEventLegend(actualValid.event.legend, expected.event.legend)
        }

        private fun <TAggregate : CtrlAggregate<TAggregate>> assertEventType(
            actual: CtrlEvent<TAggregate>,
            expected: CtrlEvent<TAggregate>
        ) {
            kotlin.test.assertEquals(actual::class, expected::class)
        }

        fun <TAggregate : CtrlAggregate<TAggregate>> assertExecution(
            actualExecution: CtrlExecution<TAggregate, CtrlEvent<TAggregate>, CtrlInvalidation>,
            expectedExecution: CtrlExecution.Invalidated<TAggregate>
        ) {
            val actualInvalid = (actualExecution as CtrlExecution.Invalidated)
            kotlin.test.assertEquals(expectedExecution.items.size, actualInvalid.items.size)
            kotlin.test.assertEquals(expectedExecution.items[0].description, actualInvalid.items[0].description)
        }

        private fun <TAggregate : CtrlAggregate<TAggregate>> assertEventLegend(actual: EventLegend<TAggregate>, expected: EventLegend<TAggregate>) {
            kotlin.test.assertEquals(expected.eventId.value, actual.eventId.value)
            kotlin.test.assertEquals(expected.aggregateId.value, actual.aggregateId.value)
            kotlin.test.assertEquals(expected.version, actual.version)
        }

        fun <TAggregate : CtrlAggregate<TAggregate>> assertAggregateEvent(model: AggregateLegend<TAggregate>, evt: CtrlEvent<TAggregate>) {
            kotlin.test.assertEquals(model.latestVersion, evt.legend.version)
        }
    }
}
