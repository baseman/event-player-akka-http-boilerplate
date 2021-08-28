package co.remotectrl.shoppingcart.api

import akka.http.javadsl.model.HttpCharsets
import akka.http.javadsl.model.HttpRequest
import akka.http.javadsl.model.MediaTypes
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.testkit.JUnitRouteTest
import co.remotectrl.shoppingcart.common.command.UpdateCommand
import co.remotectrl.shoppingcart.common.command.DiscountApply
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import com.fasterxml.jackson.databind.ObjectMapper
import co.remotectrl.shoppingcart.common.command.AddCommand
import org.junit.After
import java.time.Duration

@ContextConfiguration(
        classes = [(ApplicationConfig::class)],
        initializers = [(ShoppingCartInitializer::class)]
)
@RunWith(SpringRunner::class)
@SpringBootTest
class RouteTest : JUnitRouteTest() {

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

    @Test
    fun routes() {

        //command
        val shoppingCartAddCommandName = AddCommand::class.java.name.toLowerCase()
        testRoute(appServer.route).run(
                HttpRequest.POST("/shoppingcart/cmd").withEntity(
                        MediaTypes.applicationWithFixedCharset("vnd.$shoppingCartAddCommandName.api.v1+json", HttpCharsets.UTF_8).toContentType(),
                        ObjectMapper().writeValueAsString(AddCommand("my product",1))
                )
        ).assertStatusCode(StatusCodes.OK)

        //dto
        testRoute(appServer.route).run(
                HttpRequest.GET("/shoppingcart/items")
        )
                .assertStatusCode(StatusCodes.OK)
                .assertEntity("""[{"discountCode":null,"legend":{"latestVersion":1,"rootId":{"value":"1"}},"shoppingItems":{"my product":1}}]""")

        testRoute(appServer.route).run(
                HttpRequest.GET("/shoppingcart/item/1")
        )
                .assertStatusCode(StatusCodes.OK)
                .assertEntity("""{"discountCode":null,"legend":{"latestVersion":1,"rootId":{"value":"1"}},"shoppingItems":{"my product":1}}""")

        val myCommandName = AddCommand::class.java.name.toLowerCase()
        testRoute(appServer.route).run(
                HttpRequest.POST("/shoppingcart/cmd").withEntity(
                        MediaTypes.applicationWithFixedCharset("vnd.$myCommandName.api.v1+json", HttpCharsets.UTF_8).toContentType(),
                        ObjectMapper().writeValueAsString(AddCommand(sku = "my product", 1))
                )
        ).assertStatusCode(StatusCodes.OK)

        val discountApplyName = DiscountApply::class.java.name.toLowerCase()
        testRoute(appServer.route).run(
            HttpRequest.POST("/shoppingcart/cmd/2").withEntity(
                MediaTypes.applicationWithFixedCharset("vnd.$discountApplyName.api.v1+json", HttpCharsets.UTF_8).toContentType(),
                ObjectMapper().writeValueAsString(DiscountApply(discountCode = "my discount"))
            )
        ).assertStatusCode(StatusCodes.OK)

        testRoute(appServer.route).run(
                HttpRequest.GET("/shoppingcart/items")
        )
                .assertStatusCode(StatusCodes.OK)
                .assertEntity("""[{"discountCode":null,"legend":{"latestVersion":1,"rootId":{"value":"1"}},"shoppingItems":{"my product":1}},{"discountCode":"my discount","legend":{"latestVersion":2,"rootId":{"value":"2"}},"shoppingItems":{"my product":1}}]""")

        testRoute(appServer.route).run(
                HttpRequest.GET("/shoppingcart/item/2")
        )
                .assertStatusCode(StatusCodes.OK)
                .assertEntity("""{"discountCode":"my discount","legend":{"latestVersion":2,"rootId":{"value":"2"}},"shoppingItems":{"my product":1}}""")

        val changeCommandName = UpdateCommand::class.java.name.toLowerCase()
        testRoute(appServer.route).run(
                HttpRequest.POST("/shoppingcart/cmd/1").withEntity(
                        MediaTypes.applicationWithFixedCharset("vnd.$changeCommandName.api.v1+json", HttpCharsets.UTF_8).toContentType(),
                        ObjectMapper().writeValueAsString(UpdateCommand(sku = "my product", 2))
                )
        ).assertStatusCode(StatusCodes.OK)


        //todo: generic errors is likely a sign which testing a library maybe more suitable that testing at the api level -- identify if this is possible
        //error
        testRoute(appServer.route).run(
                HttpRequest.POST("/shoppingcart/cmd/3").withEntity(
                        MediaTypes.applicationWithFixedCharset("vnd.$changeCommandName.api.v1+json", HttpCharsets.UTF_8).toContentType(),
                        ObjectMapper().writeValueAsString(UpdateCommand("my product", 1))
                )
        ).assertStatusCode(StatusCodes.NOT_FOUND)

        testRoute(appServer.route).run(
                HttpRequest.POST("/shoppingcart/cmd").withEntity(
                        MediaTypes.applicationWithFixedCharset("vnd.$shoppingCartAddCommandName.api.v1+json", HttpCharsets.UTF_8).toContentType(),
                        ObjectMapper().writeValueAsString(AddCommand("", 0))
                )
        ).assertStatusCode(StatusCodes.BAD_REQUEST)

        testRoute(appServer.route).run(
                HttpRequest.POST("/shoppingcart/cmd/1").withEntity(
                        MediaTypes.applicationWithFixedCharset("vnd.$changeCommandName.api.v1+json", HttpCharsets.UTF_8).toContentType(),
                        ObjectMapper().writeValueAsString(UpdateCommand("", -1))
                )
        ).assertStatusCode(StatusCodes.BAD_REQUEST)


        testRoute(appServer.route).run(
                HttpRequest.GET("/shoppingcart/item/3")
        ).assertStatusCode(StatusCodes.NOT_FOUND)
    }
}