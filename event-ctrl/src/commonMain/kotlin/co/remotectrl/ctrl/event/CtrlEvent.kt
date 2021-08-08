package co.remotectrl.ctrl.event

interface CtrlEvent<TRoot : CtrlRoot<TRoot>> {

    val legend: EventLegend<TRoot>

    fun applyChangesTo(root: TRoot, latestVersion: Int): TRoot

    fun applyTo(mutable: CtrlMutable<TRoot>) {
        mutable.root = applyChangesTo(mutable.root, legend.version)
    }
}

data class CtrlMutable<TRoot : CtrlRoot<TRoot>>(var root: TRoot)

data class EventId<TRoot>(val value: String) where TRoot : CtrlRoot<TRoot>

data class EventLegend<TRoot : CtrlRoot<TRoot>>(
    val eventId: EventId<TRoot>,
    val rootId: RootId<TRoot>,
    val version: Int
) {
    constructor(evtIdVal: String, rootIdVal: String, version: Int) : this(EventId(evtIdVal), RootId(rootIdVal), version)
}
