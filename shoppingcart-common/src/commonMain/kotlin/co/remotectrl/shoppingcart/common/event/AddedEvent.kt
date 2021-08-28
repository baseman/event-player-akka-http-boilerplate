package co.remotectrl.shoppingcart.common.event

import co.remotectrl.ctrl.event.RootLegend
import co.remotectrl.ctrl.event.CtrlEvent
import co.remotectrl.ctrl.event.EventLegend
import co.remotectrl.shoppingcart.common.root.ShoppingCartRoot

data class AddedEvent(
    override val legend: EventLegend<ShoppingCartRoot>,
    val sku: String,
    val itemCount: Int
) : CtrlEvent<ShoppingCartRoot> {
    override fun applyChangesTo(root: ShoppingCartRoot, latestVersion: Int): ShoppingCartRoot {
        val updatedItems = root.shoppingItems.toMutableMap()
        updatedItems[sku] = (updatedItems[sku] ?: 0) + itemCount
        return root.copy(
            legend = root.legend.copy(latestVersion = latestVersion),
            shoppingItems = updatedItems.toMap()
        )
    }

    constructor() : this(EventLegend("", "", 0), "", -1)
}