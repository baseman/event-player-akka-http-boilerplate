package my.artifact.myeventplayer.common.command

import co.remotectrl.eventplayer.AggregateId
import co.remotectrl.eventplayer.PlayCommand
import co.remotectrl.eventplayer.PlayEvent
import my.artifact.myeventplayer.common.aggregate.MyAggregate

class MyCreateCommand : PlayCommand<MyAggregate> {

    companion object {
        const val mediaType = "vnd.my.artifact.myeventplayer.common.command.MyCreateCommand.api.v1+json"
    }

    override fun validate(model: MyAggregate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEvent(aggregateId: AggregateId<MyAggregate>, version: Int): PlayEvent<MyAggregate> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}