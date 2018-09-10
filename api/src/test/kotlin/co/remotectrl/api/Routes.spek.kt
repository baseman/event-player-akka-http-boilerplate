package co.remotectrl.api

import akka.http.javadsl.model.HttpRequest
import akka.http.javadsl.testkit.JUnitRouteTest
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.lifecycle.LifecycleListener
import org.jetbrains.spek.api.lifecycle.TestScope

class SpekRouteBootstrapper: LifecycleListener, JUnitRouteTest() {
    val system = system()

    override fun beforeExecuteTest(test: TestScope) {
        systemResource().before()
    }

    override fun afterExecuteTest(test: TestScope) {
        systemResource().after()
    }
}

class RoutesSpek : Spek({
    val bootstrapper = SpekRouteBootstrapper()
    registerListener(bootstrapper)

    val appRoute by memoized {
        bootstrapper.testRoute(Router(bootstrapper.system).createRoute())
    }

    describe("operation") {

        it("execute") {

            //todo: assert urls
            appRoute.run(HttpRequest.POST("/operation/execute"))
                    .assertStatusCode(200)
                    .assertEntity("x + y = 6.5")
        }
    }
})
