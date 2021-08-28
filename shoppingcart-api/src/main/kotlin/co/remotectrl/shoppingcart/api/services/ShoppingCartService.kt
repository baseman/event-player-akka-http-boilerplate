package co.remotectrl.shoppingcart.api.services

import co.remotectrl.ctrl.event.RootId
import co.remotectrl.ctrl.event.CtrlRoot

class ShoppingCartService {

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

    fun getRoots(items: MutableList<CtrlRoot<*>>): Array<CtrlRoot<*>> {
        return items.toTypedArray()
    }

    fun getRoot(items: MutableList<CtrlRoot<*>>, rootId: RootId<out CtrlRoot<*>>): CtrlRoot<*>? {
        return getRoots(items).find { it.legend.rootId.value == rootId.value }
    }
}