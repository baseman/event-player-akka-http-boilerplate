package co.remotectrl.shoppingcart.scenarios.load

import co.remotectrl.shoppingcart.scenarios.ShoppingCartAssert
import io.gatling.commons.validation.Validation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ShoppingCartLoad extends Simulation {

	private val httpProtocol = http
		.baseUrl("http://localhost:8080")
		.contentTypeHeader("application/vnd.co.remotectrl.shoppingcart.common.command.AddCommand.api.v1+json")

	trait Validator[A] {
		def name: String
		def apply(actual: Option[A]): Validation[Option[A]]
	}

	val headers_0 = Map("accept" -> "application/json")

	val uri1 = "http://localhost:8080"

	private val scn = scenario("SoppingCartLoad").exec(
			http("request_0")
			.post(uri1 + "/shoppingcart/cmd")
			.headers(headers_0)
			.body(
				StringBody(
					new ShoppingCartAssert().make("initial blah", 1)
				)
			)
		)

	setUp(
		scn
		.inject(atOnceUsers(1000)))
		.protocols(httpProtocol)
		.assertions(
			global.successfulRequests.percent.gt(99)
		)
}
