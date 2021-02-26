package co.remotectrl.myevent.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.myevent.assert.AssertUtil
import co.remotectrl.myevent.common.aggregate.MyAggregate
import co.remotectrl.myevent.common.command.MyChangeCommand
import co.remotectrl.myevent.common.event.MyChangedEvent

class MyAggregateTest {

    val aggregateIdVal = "1"
    val aggregateId = AggregateId<MyAggregate>(aggregateIdVal)

    val actual = MyAggregate(
            AggregateLegend(aggregateId, 1),
            "blah"
    )

    fun should_try_to_validate_Change_command_input() {

        AssertUtil.assertExecution(
                MyChangeCommand("").executeOn(actual),
                CtrlExecution.Invalidated(items = arrayOf(
                        CtrlInvalidInput("myInitialVal should not be empty")
                ))
        )

    }

    val evtIdVal = "0"
    val evtId = EventId<MyAggregate>(evtIdVal)

    val eventLegend = EventLegend(evtId, aggregateId, 2)

    fun `should produce Changed event on successful Commit command`() {
        AssertUtil.assertExecution(
                MyChangeCommand("change blah").executeOn(actual),
                CtrlExecution.Validated(event = MyChangedEvent(eventLegend, "change blah"))
        )
    }

    fun assertModel(actual: MyAggregate, expected: MyAggregate) {
        kotlin.test.assertEquals(actual.myVal, expected.myVal)
        kotlin.test.assertEquals(actual.legend.latestVersion, expected.legend.latestVersion)
    }

    fun `should apply Changed event to the MyAggregate`() {
        val evt = MyChangedEvent(EventLegend(evtId, aggregateId, 2), "blah changed")

        val actualMutableAggregate = CtrlMutableAggregate(actual)
        evt.applyTo(actualMutableAggregate)

        AssertUtil.assertAggregateEvent(actualMutableAggregate.aggregate.legend, evt)

        val expected = MyAggregate(AggregateLegend(aggregateId, 2), "blah changed")

        assertModel(actualMutableAggregate.aggregate, expected)
    }

}
