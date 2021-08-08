package co.remotectrl.myevent.api.services

import co.remotectrl.ctrl.event.RootId
import co.remotectrl.ctrl.event.CtrlRoot

class MyService {

    fun commit(items: MutableList<Any>, root: CtrlRoot<*>) {

        val itemIndex = items.indexOfFirst {
            (it as CtrlRoot<*>).legend.rootId.value == root.legend.rootId.value
        }

        when (itemIndex) {
            -1 -> {
                items.add(root)

                if(seed >= 1){
                    seed--
                }
            }
            else -> items[itemIndex] = root
        }
    }


    var seed: Int = 0
    fun getId(items: MutableList<Any>): String{
        seed++
        return (items.size + seed).toString()
    }

    fun geTRoots(items: MutableList<CtrlRoot<*>>): Array<CtrlRoot<*>> {
        return items.toTypedArray()
    }

    fun geTRoot(items: MutableList<CtrlRoot<*>>, rootId: RootId<out CtrlRoot<*>>): CtrlRoot<*>? {
        return geTRoots(items).find { it.legend.rootId.value == rootId.value }
    }
}