package co.remotectrl.myevent.common.event

import co.remotectrl.ctrl.event.CtrlEvent
import co.remotectrl.ctrl.event.EventLegend
import co.remotectrl.myevent.common.root.MyRoot

class MyComposedEvent(
    override val legend: EventLegend<MyRoot>,
    val myChangeVal: String,
    val myComposeVal: String
    ) : CtrlEvent<MyRoot> {

    override fun applyChangesTo(root: MyRoot, latestVersion: Int): MyRoot {
        return MyChangedEvent(legend, myChangeVal).applyChangesTo(root, latestVersion).let{
            it.copy(
                myVal = it.myVal + " + " + myComposeVal
            )
        }
    }

}