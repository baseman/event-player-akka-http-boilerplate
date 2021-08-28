package co.remotectrl.shoppingcart.assert

import co.remotectrl.ctrl.event.RootLegend
import co.remotectrl.ctrl.event.CtrlRoot
import co.remotectrl.ctrl.event.CtrlEvent
import co.remotectrl.ctrl.event.CtrlExecution
import co.remotectrl.ctrl.event.CtrlInvalidation
import co.remotectrl.ctrl.event.EventLegend

class AssertUtil {
    companion object {

        fun <TRoot : CtrlRoot<TRoot>> assertExecution(
            actual: CtrlExecution<TRoot, CtrlEvent<TRoot>, CtrlInvalidation>,
            expected: CtrlExecution.Validated<TRoot, CtrlEvent<TRoot>>
        ) {
            val actualValid = (actual as CtrlExecution.Validated)
            assertEventType(actual.event, expected.event)
            assertEventLegend(actualValid.event.legend, expected.event.legend)
        }

        private fun <TRoot : CtrlRoot<TRoot>> assertEventType(
            actual: CtrlEvent<TRoot>,
            expected: CtrlEvent<TRoot>
        ) {
            kotlin.test.assertEquals(actual::class, expected::class)
        }

        fun <TRoot : CtrlRoot<TRoot>> assertExecution(
            actualExecution: CtrlExecution<TRoot, CtrlEvent<TRoot>, CtrlInvalidation>,
            expectedExecution: CtrlExecution.Invalidated<TRoot>
        ) {
            val actualInvalid = (actualExecution as CtrlExecution.Invalidated)
            kotlin.test.assertEquals(expectedExecution.items.size, actualInvalid.items.size)
            kotlin.test.assertEquals(expectedExecution.items[0].description, actualInvalid.items[0].description)
        }

        private fun <TRoot : CtrlRoot<TRoot>> assertEventLegend(actual: EventLegend<TRoot>, expected: EventLegend<TRoot>) {
            kotlin.test.assertEquals(expected.eventId.value, actual.eventId.value)
            kotlin.test.assertEquals(expected.rootId.value, actual.rootId.value)
            kotlin.test.assertEquals(expected.version, actual.version)
        }

        fun <TRoot : CtrlRoot<TRoot>> assertCtrlEvent(model: RootLegend<TRoot>, evt: CtrlEvent<TRoot>) {
            kotlin.test.assertEquals(model.latestVersion, evt.legend.version)
        }
    }
}
