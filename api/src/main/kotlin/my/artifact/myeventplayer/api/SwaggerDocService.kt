package my.artifact.myeventplayer.api

import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import ch.megard.akka.http.cors.javadsl.CorsDirectives.cors
import ch.megard.akka.http.cors.javadsl.settings.CorsSettings
import com.fasterxml.jackson.core.JsonProcessingException
import io.swagger.v3.core.util.Json
import io.swagger.v3.jaxrs2.Reader
import io.swagger.v3.oas.integration.SwaggerConfiguration
import io.swagger.v3.oas.models.OpenAPI


class SwaggerDocRouter: AllDirectives() {

    fun createRoute(): Route {
        val route = route(
                path(PathMatchers.segment("api-docs").slash("swagger.json")) {
                    get {
                        complete(swaggerJson())
                    }
                })


        return cors(CorsSettings.defaultSettings()) { route }
    }

    var readerConfig = SwaggerConfiguration()
    private fun swaggerJson(): String {
        try {
            val reader = Reader(readerConfig.openAPI(OpenAPI()))
            val swagger = reader.read(MyRouter::class.java)
            return Json.pretty().writeValueAsString(swagger)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

}