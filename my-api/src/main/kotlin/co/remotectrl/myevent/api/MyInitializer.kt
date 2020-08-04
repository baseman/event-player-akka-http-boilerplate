package co.remotectrl.myevent.api

import co.remotectrl.myevent.api.actors.MyActor
import co.remotectrl.myevent.api.routing.MyRouter
import co.remotectrl.myevent.api.services.MyService
import co.remotectrl.myevent.api.spring.SpringExtension
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

class MyInitializer : ApplicationContextInitializer<GenericApplicationContext> {

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