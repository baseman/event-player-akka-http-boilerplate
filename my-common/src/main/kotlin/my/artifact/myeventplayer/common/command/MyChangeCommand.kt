package my.artifact.myeventplayer.common.command

import co.remotectrl.eventplayer.*
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.event.MyChangedEvent

data class MyChangeCommand(var myChangeVal: String) : PlayCommand<MyAggregate> {

    companion object {
        const val mediaType = "application/vnd.my.artifact.myeventplayer.common.command.MyChangeCommand.api.v1+json"
    }

    constructor() : this("")

    override fun validate(model: MyAggregate) {
        if(myChangeVal.isEmpty()){
            throw Exception("Invalid commit input")
        }
    }

    override fun getEvent(aggregateId: AggregateId<MyAggregate>, version: Int): PlayEvent<MyAggregate> {

        //todo: generate unique id
        return MyChangedEvent(EventLegend(EventId(0), aggregateId, version), myChangeVal)
    }
}