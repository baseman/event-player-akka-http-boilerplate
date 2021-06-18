package co.remotectrl.myevent.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.myevent.common.aggregate.MyAggregate
import co.remotectrl.myevent.common.command.MyCreateCommand
import co.remotectrl.myevent.common.event.MyCreatedEvent
import co.remotectrl.myevent.assert.AssertUtil

class MyCreateTest {

    val aggregateIdVal = "1"
    val aggregateId = AggregateId<MyAggregate>(aggregateIdVal)
    val actual = MyAggregate(
            legend = AggregateLegend(
                    aggregateId = aggregateId,
                    latestVersion = 1
            ),
            myVal = "blah"
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
    val eventLegend = EventLegend(
            eventId = evtId,
            aggregateId = aggregateId,
            version = 2
    )

    fun `should produce Changed Event on successful Commit Command`() {

        AssertUtil.assertExecution(
                MyCreateCommand("initial blah").executeOn(actual),
                CtrlExecution.Validated(event = MyCreatedEvent(eventLegend, "initial blah"))
        )
    }

    fun `should apply Changed Event to MyAggregate`() {
        val evt = MyCreatedEvent(EventLegend(evtId, aggregateId, 2), "blah changed")

        val active = CtrlMutable(actual)
        evt.applyTo(active)

        AssertUtil.assertAggregateEvent(active.aggregate.legend, evt)

        val expected = MyAggregate(AggregateLegend(aggregateId, 2), "blah changed")

        assertModel(active.aggregate, expected)
    }

    fun assertModel(actual: MyAggregate, expected: MyAggregate) {
        kotlin.test.assertEquals(actual.myVal, expected.myVal)
        kotlin.test.assertEquals(actual.legend.latestVersion, expected.legend.latestVersion)
    }
}
