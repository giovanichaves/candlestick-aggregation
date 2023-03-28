
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import instrument.InstrumentEvent
import instrument.InstrumentService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import persistence.InMemory
import quote.QuoteEvent
import quote.QuoteService
import java.time.Instant

private val logger = LoggerFactory.getLogger("MainKt")
const val TRACKED_MINUTES = 30
const val WEBPORT = 9000
const val WSPORT = 8032

fun Instant.trackedSlot() = (this.epochSecond / 60).toInt() % TRACKED_MINUTES

fun main() {
  logger.info("starting up")

  val storage = InMemory()
  val server = Server(storage = storage)
  val stream = Stream()
  val instrumentService = InstrumentService(storage)
  val quoteService = QuoteService(storage)

  stream.connect { event: InstrumentEvent -> runBlocking {
      logger.info("Instrument: {}", event)
      instrumentService.registerInstrumentEvent(event)
    }
  }

  stream.connect { event: QuoteEvent -> runBlocking {
      logger.info("Quote: {}", event)
      quoteService.registerQuoteEvent(event)
    }
  }


  server.start()
}

val jackson: ObjectMapper =
  jacksonObjectMapper()
    .registerModule(JavaTimeModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
