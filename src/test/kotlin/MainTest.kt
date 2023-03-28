
import instrument.Instrument
import instrument.InstrumentEvent
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers
import org.http4k.routing.WsRouter
import org.http4k.routing.bind
import org.http4k.routing.websockets
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.junit.jupiter.api.Test

class MainTest {
  fun startWs(vararg wsRoutes: WsRouter) = websockets(*wsRoutes).asServer(Netty(WSPORT)).start()

  private fun instrumentsRoute(vararg events: InstrumentEvent) = "/instruments" bind { ws: Websocket ->
    events.forEach{ ws.send(WsMessage(jackson.writeValueAsString(it))) }
  }

  @Test
  fun `receive add instrument event`() {
    val testISIN = "test123"
    val ws = startWs(
      instrumentsRoute(
        InstrumentEvent(InstrumentEvent.Type.ADD, Instrument(testISIN, "Test instrument"))
      )
    )

    main()

    Given {
      port(WEBPORT)
    } When {
      get("/instruments")
    } Then {
      statusCode(200)
      contentType(ContentType.JSON)
      body("$", CoreMatchers.equalTo(listOf(testISIN)))
    }

    ws.stop()
  }
}
