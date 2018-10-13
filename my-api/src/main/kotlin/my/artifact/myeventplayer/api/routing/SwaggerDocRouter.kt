package my.artifact.myeventplayer.api.routing

import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import ch.megard.akka.http.cors.javadsl.CorsDirectives.cors
import ch.megard.akka.http.cors.javadsl.settings.CorsSettings
import com.github.swagger.akka.javadsl.Converter
import com.github.swagger.akka.javadsl.SwaggerGenerator
import io.swagger.models.Info

class SwaggerDocRouter(apiClasses: Set<Class<*>>, apiInfo: Info): AllDirectives() {

    fun createRoute(): Route {
        val route =
                path(PathMatchers.segment("api-docs").slash("swagger.json")) {
                    get {
                        complete(generator.generateSwaggerJson())
                    }
                }

        return cors(CorsSettings.defaultSettings()) { route }
    }

    private val generator: SwaggerGenerator = object : SwaggerGenerator {
        override fun converter(): Converter {
            return Converter(this)
        }

        override fun apiClasses(): Set<Class<*>> {
            return apiClasses
        }

        override fun info(): Info {
            return apiInfo
        }

    }
}