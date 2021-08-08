package co.remotectrl.myevent.common.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.myevent.common.root.MyRoot
import co.remotectrl.myevent.common.event.MyCreatedEvent

data class MyCreateCommand(val myInitialVal: String) : CtrlCommand<MyRoot> {
    companion object {
        const val mediaType = "application/vnd.co.remotectrl.myevent.common.command.MyCreateCommand.api.v1+json"
    }
    constructor() : this("")

    override fun validate(root: MyRoot, validation: CtrlValidation) {
        validation.assert({myInitialVal.isNotEmpty()}, "myInitialVal should not be empty")
    }

    override fun getEvent(eventLegend: EventLegend<MyRoot>): CtrlEvent<MyRoot> {
        //todo: generate unique id
        return MyCreatedEvent(eventLegend, myInitialVal)
    }
}