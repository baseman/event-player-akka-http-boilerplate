package co.remotectrl.myevent.common.command

import co.remotectrl.ctrl.event.CtrlCommand
import co.remotectrl.ctrl.event.CtrlEvent
import co.remotectrl.ctrl.event.CtrlValidation
import co.remotectrl.ctrl.event.EventLegend
import co.remotectrl.myevent.common.event.MyComposedEvent
import co.remotectrl.myevent.common.root.MyRoot

data class MyComposeCommand(val myChangeVal: String, val myComposeVal: String) : CtrlCommand<MyRoot> {
    companion object {
        const val mediaType = "application/vnd.co.remotectrl.myevent.common.command.MyComposeCommand.api.v1+json"
    }
    constructor() : this("", "")

    override fun getEvent(eventLegend: EventLegend<MyRoot>): CtrlEvent<MyRoot> {
        return MyComposedEvent(eventLegend, myChangeVal, myComposeVal)
    }

    override fun validate(root: MyRoot, validation: CtrlValidation) {
        MyChangeCommand(myChangeVal).validate(root, validation)
        validation.assert({myComposeVal != myChangeVal}, "compose value cannot be change value")
    }
}