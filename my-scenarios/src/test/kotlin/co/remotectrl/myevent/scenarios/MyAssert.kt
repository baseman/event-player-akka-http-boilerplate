package co.remotectrl.myevent.scenarios

import co.remotectrl.myevent.common.command.MyCreateCommand
import com.fasterxml.jackson.databind.ObjectMapper

class MyAssert {
    companion object {
        val mapper = ObjectMapper()
    }

    fun make(myVal: String): String {
        return mapper.writeValueAsString(MyCreateCommand(myVal))
    }

    fun assertIs(strMyChangeCommand: String){
        mapper.readValue(strMyChangeCommand, MyCreateCommand::class.java)
    }
}