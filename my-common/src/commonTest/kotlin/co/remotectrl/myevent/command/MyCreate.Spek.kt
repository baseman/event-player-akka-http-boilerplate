package co.remotectrl.myevent.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.myevent.common.aggregate.MyAggregate
import co.remotectrl.myevent.common.command.MyChangeCommand
import co.remotectrl.myevent.common.command.MyCreateCommand
import co.remotectrl.myevent.common.event.MyChangedEvent
import co.remotectrl.myevent.common.event.MyCreatedEvent
import co.remotectrl.myevent.assert.AssertUtil

class MyCreateTest {

        val aggregateIdVal = "1"
        val aggregateId = AggregateId<MyAggregate>(aggregateIdVal)

        val actual = MyAggregate(
                AggregateLegend(aggregateId, 1),
                "blah"
        )

        fun `should try to validate Change Command command input`() {
            AssertUtil.assertExecution(
                    MyCreateCommand("").executeOn(actual),
                    CtrlExecution.Invalidated(items = arrayOf(
                            CtrlInvalidInput("myInitialVal should not be empty")
                    ))
            )
        }

        val evtIdVal = "0"
        val evtId = EventId<MyAggregate>(evtIdVal)

        val eventLegend = EventLegend(evtId, aggregateId, 2)

        fun `should produce Changed Event on successful Commit Command`() {

            AssertUtil.assertExecution(
                    MyCreateCommand("initial blah").executeOn(actual),
                    CtrlExecution.Validated(event = MyCreatedEvent(eventLegend, "initial blah"))
            )
        }

        fun assertModel(actual: MyAggregate, expected: MyAggregate) {
            kotlin.test.assertEquals(actual.myVal, expected.myVal)
            kotlin.test.assertEquals(actual.legend.latestVersion, expected.legend.latestVersion)
        }

        fun `should apply Changed Event to MyAggregate`() {
            val evt = MyCreatedEvent(EventLegend(evtId, aggregateId, 2), "blah changed")

            val actualMutableAggregate = CtrlMutableAggregate(actual)
            evt.applyTo(actualMutableAggregate)

            AssertUtil.assertAggregateEvent(actualMutableAggregate.aggregate.legend, evt)

            val expected = MyAggregate(AggregateLegend(aggregateId, 2), "blah changed")

            assertModel(actualMutableAggregate.aggregate, expected)
        }

}