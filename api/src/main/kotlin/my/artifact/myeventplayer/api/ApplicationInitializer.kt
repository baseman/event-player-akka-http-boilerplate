package my.artifact.myeventplayer.api

import my.artifact.myeventplayer.api.actors.MyActor
import my.artifact.myeventplayer.api.services.MyService
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

class ApplicationInitializer : ApplicationContextInitializer<GenericApplicationContext> {

    companion object BeansInitializer{
        fun get() = beans {
            bean<SpringExtension>()
            bean<ApplicationConfig>()
            bean<MyService>()
            bean("myActor") {
                MyActor(ref())
            }
            bean<MyRouter>()
            bean<ApplicationServer>()
        }
    }

    override fun initialize(context: GenericApplicationContext) {
        get().initialize(context)
    }

}