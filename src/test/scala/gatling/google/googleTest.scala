package scala.gatling.google

import scala.gatling.{Config, UserAgent}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GoogleTest extends Simulation {

  val httpConf = http
    .baseURL(Config.baseUrl)
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8") // Here are the common headers
    .acceptLanguageHeader("el-GR,el;q=0.8,fr;q=0.6")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .userAgentHeader(UserAgent.Chrome39Ubuntu)

  val scn = scenario("Google Search")

    .exec(
      http("Google Homepage")
        .get("/")
        .check(
          status.not(404),
          status.not(500)
        )
    )
    .pause(Config.thinkTime)

    .exec(http("Search for 'gatling'")
    .get("/?q=gatling"))
    .pause(Config.thinkTime)


  setUp(
    scn
      .inject(
        atOnceUsers(1),
        nothingFor(10 seconds),
        rampUsers(19) over(10 seconds),
        constantUsersPerSec(20) during(60 seconds)
      )
      .protocols(httpConf)
    )
    .assertions(
      global.responseTime.max.lessThan(100)
    )
}