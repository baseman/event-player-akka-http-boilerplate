package co.remotectrl.ctrl.event

interface CtrlEvent<TAggregate : CtrlAggregate<TAggregate>> {

    val legend: EventLegend<TAggregate>

    fun applyChangesTo(aggregate: TAggregate, latestVersion: Int): TAggregate

    fun applyTo(mutable: CtrlMutable<TAggregate>) {
        mutable.aggregate = applyChangesTo(mutable.aggregate, legend.version)
    }
}

data class CtrlMutable<TAggregate : CtrlAggregate<TAggregate>>(var aggregate: TAggregate)

data class EventId<TAggregate>(val value: String) where TAggregate : CtrlAggregate<TAggregate>

data class EventLegend<TAggregate : CtrlAggregate<TAggregate>>(
    val eventId: EventId<TAggregate>,
    val aggregateId: AggregateId<TAggregate>,
    val version: Int
) {
    constructor(evtIdVal: String, aggregateIdVal: String, version: Int) : this(EventId(evtIdVal), AggregateId(aggregateIdVal), version)
}
