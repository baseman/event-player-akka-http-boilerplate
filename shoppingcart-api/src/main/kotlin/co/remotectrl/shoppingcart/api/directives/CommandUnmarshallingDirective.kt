package co.remotectrl.shoppingcart.api.directives

import akka.http.javadsl.model.HttpCharsets
import akka.http.javadsl.model.HttpEntity
import akka.http.javadsl.model.MediaType
import akka.http.javadsl.model.MediaTypes
import akka.http.javadsl.server.Route
import akka.http.javadsl.server.directives.MarshallingDirectives
import akka.http.javadsl.unmarshalling.Unmarshaller
import co.remotectrl.ctrl.event.CtrlRoot
import co.remotectrl.ctrl.event.CtrlCommand
import com.fasterxml.jackson.databind.ObjectMapper

class CommandUnmarshallingDirective<TRoot : CtrlRoot<TRoot>> : MarshallingDirectives() {

    val deserializer = ObjectMapper()

    inline fun <reified TCommand : CtrlCommand<TRoot>> commandEntity(
            noinline inner: (TCommand) -> Route
    ): Route = entity(getCommandUnmarshaller(), inner)

    fun <TCommand> mediaType(valueType: Class<TCommand>): MediaType {
        return MediaTypes.applicationWithFixedCharset("vnd.${valueType.name.toLowerCase()}.api.v1+json", HttpCharsets.UTF_8)
    }

    inline fun <reified TCommand> getCommandUnmarshaller(): Unmarshaller<HttpEntity, TCommand> {
        val valueType: Class<TCommand> = TCommand::class.java

        return Unmarshaller.forMediaType<TCommand>(
                mediaType(valueType),
                Unmarshaller.entityToString().thenApply { jsonStr ->
                    deserializer.readValue(jsonStr, valueType)
                }
        )
    }
}
