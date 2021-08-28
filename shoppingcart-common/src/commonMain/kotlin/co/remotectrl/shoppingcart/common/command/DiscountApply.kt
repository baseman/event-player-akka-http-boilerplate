package co.remotectrl.shoppingcart.common.command

import co.remotectrl.ctrl.event.CtrlCommand
import co.remotectrl.ctrl.event.CtrlEvent
import co.remotectrl.ctrl.event.CtrlValidation
import co.remotectrl.ctrl.event.EventLegend
import co.remotectrl.shoppingcart.common.event.DiscountApplied
import co.remotectrl.shoppingcart.common.root.ShoppingCartRoot

data class DiscountApply(val discountCode: String) : CtrlCommand<ShoppingCartRoot> {
    companion object {
        const val mediaType = "application/vnd.co.remotectrl.shoppingcart.common.command.DiscountApply.api.v1+json"
    }
    constructor() : this("")

    override fun getEvent(eventLegend: EventLegend<ShoppingCartRoot>): CtrlEvent<ShoppingCartRoot> {
        return DiscountApplied(eventLegend, discountCode)
    }

    override fun validate(root: ShoppingCartRoot, validation: CtrlValidation) {
        validation.assert({discountCode != ""}, "discount code should not be empty")
    }
}