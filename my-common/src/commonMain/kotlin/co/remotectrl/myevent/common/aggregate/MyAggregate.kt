package co.remotectrl.myevent.common.aggregate

import co.remotectrl.ctrl.event.CtrlAggregate
import co.remotectrl.ctrl.event.AggregateLegend

data class MyAggregate(override val legend: AggregateLegend<MyAggregate> = AggregateLegend("", -1), val myVal: String = "") : CtrlAggregate<MyAggregate>
