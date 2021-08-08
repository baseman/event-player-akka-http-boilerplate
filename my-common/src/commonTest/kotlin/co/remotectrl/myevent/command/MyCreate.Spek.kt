package co.remotectrl.myevent.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.myevent.common.root.MyRoot
import co.remotectrl.myevent.common.command.MyCreateCommand
import co.remotectrl.myevent.common.event.MyCreatedEvent
import co.remotectrl.myevent.assert.AssertUtil

class MyCreateTest {

    val rootIdVal = "1"
    val rootId = RootId<MyRoot>(rootIdVal)
    val actual = MyRoot(
            legend = RootLegend(
                    rootId = rootId,
                    latestVersion = 1
            ),
            myVal = "blah"
    )

    fun should_try_to_validate_Change_Command_command_input() {
        AssertUtil.assertExecution(
                MyCreateCommand("").executeOn(actual),
                CtrlExecution.Invalidated(items = arrayOf(
                        CtrlInvalidInput("myInitialVal should not be empty")
                ))
        )
    }

    val evtIdVal = "0"
    val evtId = EventId<MyRoot>(evtIdVal)
    val eventLegend = EventLegend(
            eventId = evtId,
            rootId = rootId,
            version = 2
    )

    fun should_produce_Changed_Event_on_successful_Commit_Command() {

        AssertUtil.assertExecution(
                MyCreateCommand("initial blah").executeOn(actual),
                CtrlExecution.Validated(event = MyCreatedEvent(eventLegend, "initial blah"))
        )
    }

    fun should_apply_Changed_Event_to_MyRoot() {
        val evt = MyCreatedEvent(EventLegend(evtId, rootId, 2), "blah changed")

        val active = CtrlMutable(actual)
        evt.applyTo(active)

        AssertUtil.asserTRootEvent(active.root.legend, evt)

        val expected = MyRoot(RootLegend(rootId, 2), "blah changed")

        assertModel(active.root, expected)
    }

    fun assertModel(actual: MyRoot, expected: MyRoot) {
        kotlin.test.assertEquals(actual.myVal, expected.myVal)
        kotlin.test.assertEquals(actual.legend.latestVersion, expected.legend.latestVersion)
    }
}
