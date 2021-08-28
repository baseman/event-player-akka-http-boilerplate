package co.remotectrl.shoppingcart.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.shoppingcart.assert.AssertUtil
import co.remotectrl.shoppingcart.common.root.ShoppingCartRoot
import co.remotectrl.shoppingcart.common.command.DiscountApply
import co.remotectrl.shoppingcart.common.event.DiscountApplied
import kotlin.test.Test

class ShoppingCartApplyDiscountSpek {

    val rootIdVal = "1"
    val rootId = RootId<ShoppingCartRoot>(rootIdVal)

    val actual = ShoppingCartRoot(
            RootLegend(rootId, 1),
            mapOf()
    )

    @Test
    fun should_try_to_validate_DiscountApply_command_input() {

        AssertUtil.assertExecution(
                DiscountApply("").executeOn(actual),
                CtrlExecution.Invalidated(items = arrayOf(
                        CtrlInvalidInput("discount code should not be empty")
                ))
        )

    }

    val evtIdVal = "0"
    val evtId = EventId<ShoppingCartRoot>(evtIdVal)

    val eventLegend = EventLegend(evtId, rootId, 2)

    @Test
    fun should_produce_DiscountApplied_event_on_successful_Commit_command() {
        AssertUtil.assertExecution(
                DiscountApply("my discount code").executeOn(actual),
                CtrlExecution.Validated(
                    event = DiscountApplied(eventLegend, "my discount code")
                )
        )
    }

    fun assertModel(actual: ShoppingCartRoot, expected: ShoppingCartRoot) {
        kotlin.test.assertEquals(actual.shoppingItems, expected.shoppingItems)
        kotlin.test.assertEquals(actual.legend.latestVersion, expected.legend.latestVersion)
    }

    @Test
    fun should_apply_DiscountApplied_event_to_the_MyRoot() {
        val evt = DiscountApplied(
            EventLegend(evtId, rootId, 2),
            "my discount code"
        )

        val actualMutableRoot = CtrlMutable(actual)
        evt.applyTo(actualMutableRoot)

        AssertUtil.assertCtrlEvent(actualMutableRoot.root.legend, evt)

        val expected = ShoppingCartRoot(RootLegend(rootId, 2), mapOf(), "my discount code")

        assertModel(actualMutableRoot.root, expected)
    }

}
