package co.remotectrl.shoppingcart.common.root

import co.remotectrl.ctrl.event.CtrlRoot
import co.remotectrl.ctrl.event.RootLegend

data class ShoppingCartRoot(
    override val legend: RootLegend<ShoppingCartRoot> = RootLegend("", -1),
    val shoppingItems: Map<String, Int> = mapOf(),
    val discountCode: String? = null
) : CtrlRoot<ShoppingCartRoot>
