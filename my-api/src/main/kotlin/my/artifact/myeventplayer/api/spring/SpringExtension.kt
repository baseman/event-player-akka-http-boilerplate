package my.artifact.myeventplayer.api.spring

import akka.actor.*
import org.springframework.context.*
import org.springframework.stereotype.*

@Component
class SpringExtension : Extension {

    private var applicationContext: ApplicationContext? = null

    fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    internal fun props(actorBeanName: String): Props {
        return Props.create(SpringActorProducer::class.java, applicationContext, actorBeanName)
    }
}
