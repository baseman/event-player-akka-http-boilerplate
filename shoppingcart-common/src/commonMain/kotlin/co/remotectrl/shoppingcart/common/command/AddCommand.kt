package co.remotectrl.shoppingcart.common.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.shoppingcart.common.root.ShoppingCartRoot
import co.remotectrl.shoppingcart.common.event.AddedEvent

data class AddCommand(val sku: String, val itemCount: Int) : CtrlCommand<ShoppingCartRoot> {
    companion object {
        const val mediaType = "application/vnd.co.remotectrl.shoppingcart.common.command.AddCommand.api.v1+json"
    }
    constructor() : this("", -1)

    override fun validate(root: ShoppingCartRoot, validation: CtrlValidation) {
        validation.assert({sku.isNotEmpty()}, "sku should not be empty")
    }

    override fun getEvent(eventLegend: EventLegend<ShoppingCartRoot>): CtrlEvent<ShoppingCartRoot> {
        //todo: generate unique id
        return AddedEvent(eventLegend, sku, itemCount)
    }
}