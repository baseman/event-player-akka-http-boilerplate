package co.remotectrl.myevent.api

import akka.actor.ActorSystem
import co.remotectrl.myevent.api.spring.SpringExtension
import com.typesafe.config.ConfigFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean

class ApplicationConfig
constructor(
        private val applicationContext: ApplicationContext,
        private val springAkkaExtension: SpringExtension
) {

    val system: ActorSystem = ActorSystem.create("default", ConfigFactory.load())

    @Bean
    fun actorSystem(): ActorSystem {
        springAkkaExtension.setApplicationContext(applicationContext)
        return system
    }
}
