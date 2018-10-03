package my.artifact.myeventplayer.api.spring

import akka.actor.*
import org.springframework.context.*

class SpringActorProducer(private val applicationContext: ApplicationContext, private val actorBeanName: String) : IndirectActorProducer {

    override fun produce(): Actor {
        return applicationContext.getBean(actorBeanName) as Actor
    }

    override fun actorClass(): Class<out Actor> {
        return applicationContext.getType(actorBeanName) as Class<out Actor>
    }
}
