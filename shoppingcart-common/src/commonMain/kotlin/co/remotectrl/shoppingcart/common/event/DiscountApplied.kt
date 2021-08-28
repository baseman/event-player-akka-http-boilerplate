package co.remotectrl.shoppingcart.common.event

import co.remotectrl.ctrl.event.CtrlEvent
import co.remotectrl.ctrl.event.EventLegend
import co.remotectrl.shoppingcart.common.root.ShoppingCartRoot

class DiscountApplied(
    override val legend: EventLegend<ShoppingCartRoot>,
    val discountCode: String
    ) : CtrlEvent<ShoppingCartRoot> {

    override fun applyChangesTo(root: ShoppingCartRoot, latestVersion: Int): ShoppingCartRoot {
        return root.copy(
            legend = root.legend.copy(latestVersion = latestVersion),
            discountCode = discountCode
        )
    }

}