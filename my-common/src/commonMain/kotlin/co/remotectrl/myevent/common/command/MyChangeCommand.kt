package co.remotectrl.myevent.common.command

import co.remotectrl.eventplayer.*
import co.remotectrl.myevent.common.aggregate.MyAggregate
import co.remotectrl.myevent.common.event.MyChangedEvent

data class MyChangeCommand(var myChangeVal: String) : PlayCommand<MyAggregate> {
    companion object {

        const val mediaType = "application/vnd.co.remotectrl.myevent.common.command.MyChangeCommand.api.v1+json"
    }
    constructor() : this("")

    override fun validate(aggregate: MyAggregate, validation: PlayValidation) {
        validation.assert({myChangeVal.isNotEmpty()}, "myInitialVal should not be empty")
    }

    override fun getEvent(aggregateId: AggregateId<MyAggregate>, version: Int): PlayEvent<MyAggregate> {

        //todo: generate unique id
        return MyChangedEvent(EventLegend(EventId(0), aggregateId, version), myChangeVal)
    }
}