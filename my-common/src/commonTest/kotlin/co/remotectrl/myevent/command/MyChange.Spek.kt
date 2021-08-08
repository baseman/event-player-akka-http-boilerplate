package co.remotectrl.myevent.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.myevent.assert.AssertUtil
import co.remotectrl.myevent.common.root.MyRoot
import co.remotectrl.myevent.common.command.MyChangeCommand
import co.remotectrl.myevent.common.event.MyChangedEvent
import kotlin.test.Test

class MyRootTest {

    val rootIdVal = "1"
    val rootId = RootId<MyRoot>(rootIdVal)

    val actual = MyRoot(
            RootLegend(rootId, 1),
            "blah"
    )

    @Test
    fun should_try_to_validate_Change_command_input() {

        AssertUtil.assertExecution(
                MyChangeCommand("").executeOn(actual),
                CtrlExecution.Invalidated(items = arrayOf(
                        CtrlInvalidInput("myInitialVal should not be empty")
                ))
        )

    }

    val evtIdVal = "0"
    val evtId = EventId<MyRoot>(evtIdVal)

    val eventLegend = EventLegend(evtId, rootId, 2)

    @Test
    fun should_produce_Changed_event_on_successful_Commit_command() {
        AssertUtil.assertExecution(
                MyChangeCommand("change blah").executeOn(actual),
                CtrlExecution.Validated(event = MyChangedEvent(eventLegend, "change blah"))
        )
    }

    fun assertModel(actual: MyRoot, expected: MyRoot) {
        kotlin.test.assertEquals(actual.myVal, expected.myVal)
        kotlin.test.assertEquals(actual.legend.latestVersion, expected.legend.latestVersion)
    }

    @Test
    fun should_apply_Changed_event_to_the_MyRoot() {
        val evt = MyChangedEvent(EventLegend(evtId, rootId, 2), "blah changed")

        val actualMutableRoot = CtrlMutable(actual)
        evt.applyTo(actualMutableRoot)

        AssertUtil.asserTRootEvent(actualMutableRoot.root.legend, evt)

        val expected = MyRoot(RootLegend(rootId, 2), "blah changed")

        assertModel(actualMutableRoot.root, expected)
    }

}
