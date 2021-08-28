package co.remotectrl.shoppingcart.common.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.shoppingcart.common.root.ShoppingCartRoot
import co.remotectrl.shoppingcart.common.event.UpdatedEvent

data class UpdateCommand(var sku: String, val itemCount: Int) : CtrlCommand<ShoppingCartRoot> {
    companion object {
        const val mediaType = "application/vnd.co.remotectrl.shoppingcart.common.command.UpdateCommand.api.v1+json"
    }
    constructor() : this("", -1)

    override fun validate(root: ShoppingCartRoot, validation: CtrlValidation) {
        validation.assert({sku.isNotEmpty()}, "sku should not be empty")
        validation.assert({itemCount > -1 }, "item count should be 0 or more")
    }

    override fun getEvent(eventLegend: EventLegend<ShoppingCartRoot>): CtrlEvent<ShoppingCartRoot> {
        //todo: generate unique id
        return UpdatedEvent(eventLegend, sku, itemCount)
    }
}