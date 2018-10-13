package my.artifact.myeventplayer.api

import org.slf4j.LoggerFactory
import sun.misc.Signal
import sun.misc.SignalHandler
import java.util.concurrent.atomic.AtomicBoolean

class ApplicationGracefulShutdownHandler(val fShutdown: () -> Unit): SignalHandler {

    private val SIGINT = "INT"
    private val SIGTERM = "TERM"

    private val log = LoggerFactory.getLogger(MyApplication::class.java)

    var isShuttingDown = AtomicBoolean(false)
        private set

    init {
        Signal.handle(Signal(SIGTERM), this)
        Signal.handle(Signal(SIGINT), this)
    }

    override fun handle(signal: Signal?) {

        log.debug("handling request to onShutdown")

        if (!isShuttingDown.compareAndSet(false, true)) return

        isShuttingDown.set(true)

        log.debug("Termination required, swallowing SIGINT/SIGTERM to allow graceful onShutdown")

        if (!arrayOf(SIGINT, SIGTERM).contains(signal?.name)) return

        fShutdown()
    }

}