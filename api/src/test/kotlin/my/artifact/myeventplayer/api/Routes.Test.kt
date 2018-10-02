package my.artifact.myeventplayer.api

import akka.http.javadsl.model.HttpRequest
import akka.http.javadsl.model.MediaTypes
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.testkit.JUnitRouteTest
import my.artifact.myeventplayer.common.command.MyChangeCommand
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import com.fasterxml.jackson.databind.ObjectMapper


@ContextConfiguration(
        classes = [(ApplicationConfig::class)],
        initializers = [(MyInitializer::class)]
)
@RunWith(SpringRunner::class)
@SpringBootTest
class RoutesTest : JUnitRouteTest() {

    @Autowired
    lateinit var appServer: ApplicationServer

    @Before
    fun initClass() {
        appServer.init()
    }

//    @After
//    fun afterAll() {}

    @Test
    fun execute() {

        testRoute(appServer.route).run(
                HttpRequest.POST("/my/1/cmd").withEntity(
                        MediaTypes.APPLICATION_JSON.toContentType(),
                        ObjectMapper().writeValueAsString(MyChangeCommand("blah"))
                        )
        ).assertStatusCode(StatusCodes.OK)
    }
}