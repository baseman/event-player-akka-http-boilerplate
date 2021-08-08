package co.remotectrl.myevent.common.command

import co.remotectrl.ctrl.event.*
import co.remotectrl.myevent.common.root.MyRoot
import co.remotectrl.myevent.common.event.MyChangedEvent

data class MyChangeCommand(var myChangeVal: String) : CtrlCommand<MyRoot> {
    companion object {

        const val mediaType = "application/vnd.co.remotectrl.myevent.common.command.MyChangeCommand.api.v1+json"
    }
    constructor() : this("")

    override fun validate(root: MyRoot, validation: CtrlValidation) {
        validation.assert({myChangeVal.isNotEmpty()}, "myInitialVal should not be empty")
    }

    override fun getEvent(eventLegend: EventLegend<MyRoot>): CtrlEvent<MyRoot> {
        //todo: generate unique id
        return MyChangedEvent(eventLegend, myChangeVal)
    }
}