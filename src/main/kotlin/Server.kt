
import candlestick.CandlestickManager
import candlestick.CandlestickManagerService
import kotlinx.coroutines.runBlocking
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Netty
import org.http4k.server.asServer
import persistence.CandlestickStorage
import quote.NonExistingInstrumentException

class Server(
  port: Int = WEBPORT,
  storage: CandlestickStorage
) {

  private val candlestickManager : CandlestickManager = CandlestickManagerService(storage)
  private val routes = routes(
    "candlesticks" bind Method.GET to { getCandlesticks(it) }
  )

  private val server: Http4kServer = routes.asServer(Netty(port))

  fun start() {
    server.start()
  }

  private fun getCandlesticks(req: Request): Response = runBlocking {
    val isin = req.query("isin")
      ?: return@runBlocking Response(Status.BAD_REQUEST).body("{'reason': 'missing_isin'}")

    return@runBlocking try {
      val candlesticks = candlestickManager.getCandlesticks(isin)
      val body = jackson.writeValueAsBytes(candlesticks)
      Response(Status.OK).header("Content-type", "application/json").body(body.inputStream())
    } catch (e: NonExistingInstrumentException) {
      Response(Status.BAD_REQUEST).body("Instrument $isin does not exist")
    }
  }
}
