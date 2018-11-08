package my.artifact.myeventplayer.common.command

import co.remotectrl.eventplayer.*
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.event.MyCreatedEvent

data class MyCreateCommand(val myInitialVal: String) : PlayCommand<MyAggregate> {

    companion object {
        const val mediaType = "vnd.my.artifact.myeventplayer.common.command.MyCreateCommand.api.v1+json"
    }

    constructor() : this("")

    override fun validate(model: MyAggregate) {
        if(myInitialVal.isEmpty()){
            throw Exception("Invalid commit input")
        }
    }

    override fun getEvent(aggregateId: AggregateId<MyAggregate>, version: Int): PlayEvent<MyAggregate> {
        //todo: generate unique id
        return MyCreatedEvent(EventLegend(EventId(0), aggregateId, version), myInitialVal)
    }
}