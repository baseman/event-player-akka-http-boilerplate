package my.artifact.myeventplayer.api

import akka.actor.*
import com.typesafe.config.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.*
import org.springframework.context.annotation.*

@Configuration
class ApplicationConfig @Autowired
constructor(private val applicationContext: ApplicationContext, private val springAkkaExtension: SpringExtension) {

    val system: ActorSystem = ActorSystem.create("default", ConfigFactory.load())

    @Bean
    fun actorSystem(): ActorSystem {
        springAkkaExtension.setApplicationContext(applicationContext)
        return system
    }
}
