package my.artifact.myeventplayer.common.command

import co.remotectrl.eventplayer.*
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.event.MyCreatedEvent

data class MyCreateCommand(val myInitialVal: String) : PlayCommand<MyAggregate> {
    companion object {

        const val mediaType = "application/vnd.my.artifact.myeventplayer.common.command.MyCreateCommand.api.v1+json"
    }
    constructor() : this("")

    override fun validate(aggregate: MyAggregate, validation: PlayValidation) {
        validation.assert({myInitialVal.isNotEmpty()}, "myInitialVal should not be empty")
    }

    override fun getEvent(aggregateId: AggregateId<MyAggregate>, version: Int): PlayEvent<MyAggregate> {
        //todo: generate unique id
        return MyCreatedEvent(EventLegend(EventId(0), aggregateId, version), myInitialVal)
    }
}