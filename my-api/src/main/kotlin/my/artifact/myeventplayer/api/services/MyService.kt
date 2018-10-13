package my.artifact.myeventplayer.api.services

import co.remotectrl.eventplayer.Aggregate
import co.remotectrl.eventplayer.AggregateId
import co.remotectrl.eventplayer.PlayCommand
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import org.springframework.stereotype.Service

@Service
class MyService {
    fun something(x: AggregateId<out Aggregate<*>>, command: PlayCommand<out Aggregate<*>>) {
        System.out.println(String.format("Command Executed for Aggregate %s.", x.value))
    }
}
