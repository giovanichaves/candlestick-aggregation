package persistence

import candlestick.Candlestick
import instrument.Instrument
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import trackedSlot
import java.time.Instant
import java.time.temporal.ChronoUnit

class InMemoryTest {

    val storage = InMemory()
    @Test
    fun `does not overwrite candlesticks adding already existing instrument`() {
        val isin = "test123"
        val now = Instant.now()
        val firstInsert = storage.storeInstrument(Instrument(isin, "test"))

        val candlesticks = mutableMapOf(
            now.trackedSlot() to Candlestick(
                openTimestamp = now.truncatedTo(ChronoUnit.MINUTES),
                closeTimestamp = now.plusSeconds(60).truncatedTo(ChronoUnit.MINUTES),
                openPrice = 1.0,
                highPrice = 3.0,
                lowPrice = 1.0,
                closingPrice = 2.0
            )
        )

        storage.storeCandlesticks(isin, candlesticks)
        val firstCandlesticks = storage.getCandlesticks(isin)

        val secondInsert = storage.storeInstrument(Instrument(isin, "test"))
        val secondCandlesticks = storage.getCandlesticks(isin)

        firstInsert shouldBe true
        firstCandlesticks shouldBe candlesticks
        secondInsert shouldBe false
        secondCandlesticks shouldBe candlesticks
    }

    @Test
    fun `removes instrument`() {
        val isin = "test123"
        val now = Instant.now()
        val instrumentInsert = storage.storeInstrument(Instrument(isin, "test"))

        val candlestick = Candlestick(
            openTimestamp = now.truncatedTo(ChronoUnit.MINUTES),
            closeTimestamp = now.plusSeconds(60).truncatedTo(ChronoUnit.MINUTES),
            openPrice = 1.0,
            highPrice = 3.0,
            lowPrice = 1.0,
            closingPrice = 2.0
        )
        val candlesticks = mutableMapOf(
            now.trackedSlot() to candlestick
        )

        storage.storeCandlesticks(isin, candlesticks)
        val candlesticksBeforeDelete = storage.getCandlesticks(isin)

        val instrumentDelete = storage.deleteInstrument(isin)
        val candlesticksAfterDelete = storage.getCandlesticks(isin)

        instrumentInsert shouldBe true
        candlesticksBeforeDelete shouldBe candlesticks

        instrumentDelete shouldBe true
        candlesticksAfterDelete shouldBe null
    }

    @Test
    fun `fails removing not existing instrument`() {
        storage.deleteInstrument("not-existing") shouldBe false
    }

}