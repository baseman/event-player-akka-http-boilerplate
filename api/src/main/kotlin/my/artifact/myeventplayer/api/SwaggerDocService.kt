package my.artifact.myeventplayer.api

import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import ch.megard.akka.http.cors.javadsl.CorsDirectives.cors
import ch.megard.akka.http.cors.javadsl.settings.CorsSettings
import com.github.swagger.akka.javadsl.Converter
import com.github.swagger.akka.javadsl.SwaggerGenerator
import io.swagger.models.Info


class SwaggerDocRouter(apiClasses: Set<Class<*>>): AllDirectives() {

    var generator: SwaggerGenerator = object : SwaggerGenerator {
        override fun converter(): Converter {
            return Converter(this)
        }

        override fun apiClasses(): Set<Class<*>> {
            return apiClasses
        }

        override fun info(): Info {
            return Info()
                    .title("My API")
                    .description("Simple akka-http application")
                    .version("1.0")
        }
    }

    fun createRoute(): Route {
        val route =
                path(PathMatchers.segment("api-docs").slash("swagger.json")) {
                    get {
                        complete(generator.generateSwaggerJson())
                    }
                }

        return cors(CorsSettings.defaultSettings()) { route }
    }

//    var readerConfig = SwaggerConfiguration()
//    private fun swaggerJson(): String {
//        try {
//            val reader = Reader(readerConfig.openAPI(OpenAPI()))
//            val swagger = reader.read(MyRouter::class.java)
//            return Json.pretty().writeValueAsString(swagger)
//        } catch (e: JsonProcessingException) {
//            throw RuntimeException(e)
//        }
//    }

}