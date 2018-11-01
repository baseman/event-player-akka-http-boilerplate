package my.artifact.myeventplayer.api

import akka.http.javadsl.model.HttpCharsets
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
import org.junit.After
import java.time.Duration


@ContextConfiguration(
        classes = [(ApplicationConfig::class)],
        initializers = [(MyInitializer::class)]
)
@RunWith(SpringRunner::class)
@SpringBootTest
class CommandRoutesTest : JUnitRouteTest() {

    @Autowired
    lateinit var appServer: ApplicationServer

    @Before
    fun before() {
        appServer.bind()
    }

    @After
    fun after() {
        appServer.onShutdown { binding, _ ->
            binding.terminate(
                    Duration.ofSeconds(1)
            )
        }
    }

//    @After
//    fun afterAll() {}


    @Test
    fun routes() {

        //command
        val commandName = MyChangeCommand::class.java.name
        testRoute(appServer.route).run(
                HttpRequest.POST("/my/1/cmd").withEntity(
                        MediaTypes.applicationWithFixedCharset("vnd.$commandName.api.v1+json", HttpCharsets.UTF_8).toContentType(),
                        ObjectMapper().writeValueAsString(MyChangeCommand("blah"))
                        )
        ).assertStatusCode(StatusCodes.OK)

        //dto
        testRoute(appServer.route).run(
                HttpRequest.GET("/my")
        ).assertStatusCode(StatusCodes.OK)
//                .assertEntity("x + y = 6.5")

        testRoute(appServer.route).run(
                HttpRequest.GET("/my/1")
        ).assertStatusCode(StatusCodes.OK)
////                .assertEntity("x + y = 6.5")
    }
}