package my.artifact.myeventplayer.api

import akka.actor.*
import com.typesafe.config.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.*
import org.springframework.context.annotation.*

@Configuration
class ApplicationConfig @Autowired
constructor(private val applicationContext: ApplicationContext, private val springAkkaExtension: SpringExtension) {

    @Bean
    fun actorSystem(): ActorSystem {
        val system = ActorSystem.create("default", akkaConfiguration())
        springAkkaExtension.setApplicationContext(applicationContext)
        return system
    }

    @Bean
    fun akkaConfiguration(): Config {
        return ConfigFactory.load()
    }
}
