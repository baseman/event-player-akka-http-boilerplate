package co.remotectrl.shoppingcart.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.shoppingcart.assert.AssertUtil
import co.remotectrl.shoppingcart.common.root.ShoppingCartRoot
import co.remotectrl.shoppingcart.common.command.UpdateCommand
import co.remotectrl.shoppingcart.common.event.UpdatedEvent
import kotlin.test.Test

class ShoppingCartUpdateSpek {

    val rootIdVal = "1"
    val rootId = RootId<ShoppingCartRoot>(rootIdVal)

    val actual = ShoppingCartRoot(
        RootLegend(rootId, 1),
        mapOf("blah changed" to 2)
    )

    @Test
    fun should_try_to_validate_Change_command_input() {

        AssertUtil.assertExecution(
            UpdateCommand("", -1).executeOn(actual),
            CtrlExecution.Invalidated(
                items = arrayOf(
                    CtrlInvalidInput("sku should not be empty"),
                    CtrlInvalidInput("item count should be 0 or more")
                )
            )
        )

    }

    val evtIdVal = "0"
    val evtId = EventId<ShoppingCartRoot>(evtIdVal)

    val eventLegend = EventLegend(evtId, rootId, 2)

    @Test
    fun should_produce_Changed_event_on_successful_Commit_command() {
        AssertUtil.assertExecution(
            UpdateCommand("change blah", 1).executeOn(actual),
            CtrlExecution.Validated(event = UpdatedEvent(eventLegend, "change blah", 1))
        )
    }

    fun assertModel(actual: ShoppingCartRoot, expected: ShoppingCartRoot) {
        kotlin.test.assertEquals(actual.shoppingItems, expected.shoppingItems)
        kotlin.test.assertEquals(actual.legend.latestVersion, expected.legend.latestVersion)
    }

    @Test
    fun should_apply_Changed_event_to_the_MyRoot() {
        val evt = UpdatedEvent(EventLegend(evtId, rootId, 2), "blah changed", 3)

        val actualMutableRoot = CtrlMutable(actual)
        evt.applyTo(actualMutableRoot)

        AssertUtil.assertCtrlEvent(actualMutableRoot.root.legend, evt)

        val expected = ShoppingCartRoot(RootLegend(rootId, 2), mapOf("blah changed" to 3))

        assertModel(actualMutableRoot.root, expected)
    }

}
