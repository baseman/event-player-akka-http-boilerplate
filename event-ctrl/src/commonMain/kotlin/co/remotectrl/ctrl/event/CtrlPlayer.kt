package co.remotectrl.ctrl.event

class CtrlPlayer<TAggregate : CtrlAggregate<TAggregate>> {
    fun playFor(evts: Array<CtrlEvent<TAggregate>>, aggregate: TAggregate) {
        for (evt in evts) {
            evt.applyTo(CtrlMutableAggregate(aggregate = aggregate))
        }
    }
}
