package co.remotectrl.shoppingcart.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.shoppingcart.common.root.ShoppingCartRoot
import co.remotectrl.shoppingcart.common.command.AddCommand
import co.remotectrl.shoppingcart.common.event.AddedEvent
import co.remotectrl.shoppingcart.assert.AssertUtil

class ShoppingCartAddSpek {

    val rootIdVal = "1"
    val rootId = RootId<ShoppingCartRoot>(rootIdVal)
    val actual = ShoppingCartRoot(
        legend = RootLegend(
            rootId = rootId,
            latestVersion = 1
        ),
        shoppingItems = mapOf(Pair("my product", 1))
    )

    fun should_try_to_validate_Add_Command_command_input() {
        AssertUtil.assertExecution(
            AddCommand("", 0).executeOn(actual),
            CtrlExecution.Invalidated(
                items = arrayOf(
                    CtrlInvalidInput("sku should not be empty"),
                    CtrlInvalidInput("itemCount should be greater than 0")
                )
            )
        )
    }

    val evtIdVal = "0"
    val evtId = EventId<ShoppingCartRoot>(evtIdVal)
    val eventLegend = EventLegend(
        eventId = evtId,
        rootId = rootId,
        version = 2
    )

    fun should_produce_Added_Event_on_successful_Commit_Command() {

        AssertUtil.assertExecution(
            AddCommand("initial blah", 1).executeOn(actual),
            CtrlExecution.Validated(event = AddedEvent(eventLegend, "initial blah", 1))
        )
    }

    fun should_apply_Added_Event_to_MyRoot() {
        val evt = AddedEvent(EventLegend(evtId, rootId, 2), "my sku", 2)

        val active = CtrlMutable(actual)
        evt.applyTo(active)

        AssertUtil.assertCtrlEvent(active.root.legend, evt)

        val expected = ShoppingCartRoot(RootLegend(rootId, 2), mapOf(Pair("my product", 3)))

        assertModel(active.root, expected)
    }

    fun assertModel(actual: ShoppingCartRoot, expected: ShoppingCartRoot) {
        kotlin.test.assertEquals(actual.shoppingItems, expected.shoppingItems)
        kotlin.test.assertEquals(actual.legend.latestVersion, expected.legend.latestVersion)
    }
}
