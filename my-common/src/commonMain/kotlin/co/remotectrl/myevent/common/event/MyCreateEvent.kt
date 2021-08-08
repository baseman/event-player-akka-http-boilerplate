package co.remotectrl.myevent.common.event

import co.remotectrl.ctrl.event.RootLegend
import co.remotectrl.ctrl.event.CtrlEvent
import co.remotectrl.ctrl.event.EventLegend
import co.remotectrl.myevent.common.root.MyRoot

data class MyCreatedEvent(
    override val legend: EventLegend<MyRoot>,
    val myInitialVal: String
) : CtrlEvent<MyRoot> {
    override fun applyChangesTo(root: MyRoot, latestVersion: Int): MyRoot {
        return MyRoot(
                RootLegend(legend.rootId, latestVersion),
                myVal = myInitialVal
        )
    }

    constructor() : this(EventLegend("", "", 0), "")
}