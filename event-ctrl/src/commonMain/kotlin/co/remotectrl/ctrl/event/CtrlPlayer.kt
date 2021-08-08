package co.remotectrl.ctrl.event

class CtrlPlayer<TRoot : CtrlRoot<TRoot>> {
    fun playFor(evts: Array<CtrlEvent<TRoot>>, root: TRoot) {
        for (evt in evts) {
            evt.applyTo(CtrlMutable(root = root))
        }
    }
}
