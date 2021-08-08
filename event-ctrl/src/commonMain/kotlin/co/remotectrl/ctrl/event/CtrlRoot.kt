package co.remotectrl.ctrl.event

interface CtrlRoot<TRoot : CtrlRoot<TRoot>> {
    val legend: RootLegend<TRoot>
}

data class RootId<TRoot>(val value: String) where TRoot : CtrlRoot<TRoot>

data class RootLegend<TRoot : CtrlRoot<TRoot>>(
    val rootId: RootId<TRoot>,
    val latestVersion: Int
) {
    constructor(
        rootIdVal: String,
        latestVersion: Int
    ) : this(RootId(value = rootIdVal), latestVersion = latestVersion)
}
