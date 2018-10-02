package my.artifact.myeventplayer.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MyApplication : ApplicationRunner{

    private val log = LoggerFactory.getLogger(MyApplication::class.java)

    @Autowired
    private val appServer: ApplicationServer? = null

    override fun run(args: ApplicationArguments?) {
        log.info("Your application started")

        this.appServer?.init()
    }
}

fun main(args: Array<String>) {
    try {
        runApplication<MyApplication>(*args) {
            addInitializers(MyInitializer())
        }
    }
    catch (e: Exception) {
        System.out.println("err: $e")
        throw e
    }
}