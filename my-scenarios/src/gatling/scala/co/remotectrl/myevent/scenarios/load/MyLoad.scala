package co.remotectrl.myevent.scenarios.load

import co.remotectrl.myevent.scenarios.MyAssert
import io.gatling.commons.validation.Validation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MyLoad extends Simulation {

	private val httpProtocol = http
		.baseUrl("http://localhost:8080")
		.contentTypeHeader("application/vnd.co.remotectrl.myevent.common.command.MyCreateCommand.api.v1+json")

	trait Validator[A] {
		def name: String
		def apply(actual: Option[A]): Validation[Option[A]]
	}

	val headers_0 = Map("accept" -> "application/json")

	val uri1 = "http://localhost:8080"

	private val scn = scenario("MyLoad").exec(
			http("request_0")
			.post(uri1 + "/my/cmd")
			.headers(headers_0)
			.body(
				StringBody(
					new MyAssert().make("initial blah")
				)
			)
		)

	setUp(
		scn
		.inject(atOnceUsers(1)))
		.protocols(httpProtocol)
		.assertions(
			global.successfulRequests.percent.gt(99)
		)
}
