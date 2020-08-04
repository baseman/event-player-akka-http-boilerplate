package co.remotectrl.myevent.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.Duration

@SpringBootApplication
class MyApplication : ApplicationRunner{

    @Autowired
    private val appServer: ApplicationServer? = null

    override fun run(args: ApplicationArguments?) {
        this.appServer?.let {
            ApplicationGracefulShutdownHandler {
                it.onShutdown { binding, system ->
                    binding.terminate(
                            Duration.ofSeconds(3)
                    ).thenAccept { _ ->

                        // hook for akka, will be used for any stops, not only by signals
                        system.registerOnTermination {
                            System.exit(0)
                        }

                        system.terminate()
                    }
                }
            }

            it.bind()
        }
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