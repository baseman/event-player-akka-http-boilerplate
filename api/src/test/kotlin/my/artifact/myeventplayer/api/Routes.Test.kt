package my.artifact.myeventplayer.api

import akka.http.javadsl.model.HttpRequest
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.testkit.JUnitRouteTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@ContextConfiguration(
        classes = [(ApplicationConfig::class)],
        initializers = [(ApplicationInitializer::class)]
)
@RunWith(SpringRunner::class)
@SpringBootTest
class RoutesTest : JUnitRouteTest() {

    @Autowired
    lateinit var appServer: ApplicationServer

    @Before
    fun beforeAll() {
        appServer.init()
    }

//    @After
//    fun afterAll() {}

    @Test
    fun `execute`() {
        System.out.println("blah blah")
        testRoute(appServer.route).run(HttpRequest.POST("/operation"))
                .assertStatusCode(StatusCodes.OK)
//                .assertMediaType("application/json")
                .assertEntity("<h1>Hello World</h1>");
    }
}