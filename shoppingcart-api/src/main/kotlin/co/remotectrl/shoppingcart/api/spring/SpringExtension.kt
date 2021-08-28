package co.remotectrl.shoppingcart.api.spring

import akka.actor.Extension
import akka.actor.Props
import org.springframework.context.ApplicationContext

class SpringExtension : Extension {

    private var applicationContext: ApplicationContext? = null

    fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    internal fun props(actorBeanName: String): Props {
        return Props.create(SpringActorProducer::class.java, applicationContext, actorBeanName)
    }
}
