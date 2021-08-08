package co.remotectrl.myevent.common.root

import co.remotectrl.ctrl.event.CtrlRoot
import co.remotectrl.ctrl.event.RootLegend

data class MyRoot(
    override val legend: RootLegend<MyRoot> = RootLegend("", -1),
    val myVal: String = ""
) : CtrlRoot<MyRoot>
