package co.remotectrl.shoppingcart.scenarios

import co.remotectrl.shoppingcart.common.command.AddCommand
import com.fasterxml.jackson.databind.ObjectMapper

class ShoppingCartAssert {
    companion object {
        val mapper = ObjectMapper()
    }

    fun make(sku: String, itemCount: Int): String {
        return mapper.writeValueAsString(AddCommand(sku, itemCount))
    }

    fun assertIs(strAddCommand: String){
        mapper.readValue(strAddCommand, AddCommand::class.java)
    }
}