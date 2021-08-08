package co.remotectrl.myevent.api.actors

import akka.actor.AbstractActor
import akka.event.Logging
import co.remotectrl.ctrl.event.RootId
import co.remotectrl.ctrl.event.CtrlRoot
import co.remotectrl.myevent.api.services.MyService
import org.springframework.context.annotation.Scope

@Scope("prototype")
class MyActor(private val myService: MyService) : AbstractActor() {

    private val log = Logging.getLogger(context.system(), this)

    class MyRepository {
        companion object {
            val items = mutableListOf<Any>()
        }
    }

    var seed: Int = 0

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(RootCommandMessages.Persist::class.java) {

                    try {
                        myService.commit(
                                MyRepository.items,
                                root = it.root
                        )

                        sender.tell(RootCommandMessages.ActionPerformed(), self)

                    } catch (e: Error) {
                        sender.tell(RootCommandMessages.ActionPerformed(), self)
                    }

                }
                .match(RootDtoMessages.GetNewId::class.java) {
                    sender.tell(RootDtoMessages.ReturnId(value = myService.getId(MyRepository.items)), self)
                }
                .match(RootDtoMessages.GetItems::class.java) { getMsgs ->

                    sender.let {
                        val items = myService.geTRoots(MyRepository.items as MutableList<CtrlRoot<*>>)
                        it.tell(RootDtoMessages.ReturnItems(items = items), self)
                    }

                }
                .match(RootDtoMessages.GetItem::class.java) { getMsg ->

                    sender.let {
                        val item = myService.geTRoot(MyRepository.items as MutableList<CtrlRoot<*>>, getMsg.rootId)
                        it.tell(RootDtoMessages.ReturnItem(item = item), self)
                    }

                }
                .matchAny {
                    log.info("received unknown message")
                }.build()
    }

}

interface RootDtoMessages{
    class GetItem<TRoot : CtrlRoot<TRoot>>(val rootId: RootId<TRoot>)
    class GetItems
    class ReturnItem<TRoot : CtrlRoot<*>>(val item: TRoot?)
    class ReturnItems(val items: Array<CtrlRoot<*>>)
    class GetNewId
    class ReturnId(val value: String)
}

interface RootCommandMessages{
    class Persist<TRoot : CtrlRoot<TRoot>>(val root: TRoot)
    class ActionPerformed
}