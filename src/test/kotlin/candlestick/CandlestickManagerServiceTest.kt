package candlestick

import TRACKED_MINUTES
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import persistence.CandlestickStorage
import persistence.InMemory
import java.time.Instant
import java.time.temporal.ChronoUnit

class CandlestickManagerServiceTest {
    val storage = mockk<CandlestickStorage>()

    val service = CandlestickManagerService(storage)

    val candlestick = { now: Instant ->
        Candlestick(
            openTimestamp = now.truncatedTo(ChronoUnit.MINUTES),
            closeTimestamp = now.plusSeconds(60).truncatedTo(ChronoUnit.MINUTES),
            openPrice = 1.0,
            highPrice = 3.0,
            lowPrice = 1.0,
            closingPrice = 2.0
        )
    }

    @Test
    fun `finds first usable candlestick on time limit`() {
        val now = Instant.now()

        val candlesticks = listOf(
            candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.5).toLong())),
            candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.2).toLong())),
            candlestick(now.minusSeconds(TRACKED_MINUTES * 60L)),
            candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 0.5).toLong())),
            candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 0.1).toLong())),
        )

        val lastValidCandlestick = with (CandlestickManagerService(InMemory())) {
            candlesticks.getFirstValidCandlestick(now)
        }

        lastValidCandlestick shouldBe candlesticks[2]
    }

    @Test
    fun `finds first usable candlestick over time limit`() {
        val now = Instant.now()

        val candlesticks = listOf(
            candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.5).toLong())),
            candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.2).toLong())),
            candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 0.5).toLong())),
            candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 0.1).toLong())),
        )

        val lastValidCandlestick = with(service) {
            candlesticks.getFirstValidCandlestick(now)
        }

        lastValidCandlestick shouldBe candlesticks[1]
    }

    @Test
    fun `returns candlesticks history when last candlestick is before limit`() = runBlocking {
        val now = Instant.now()
        val candlesticks = mutableMapOf(
            1 to candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.8).toLong())),
            2 to candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.5).toLong())),
            3 to candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.4).toLong())),
            4 to candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.3).toLong())),
            5 to candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.2).toLong())),
        )

        every { storage.getCandlesticks(any()) } returns candlesticks

        val candlestickHistory = service.getCandlesticks("test")

        val expectedHistory = mutableListOf<Candlestick>()

        for (minute: Long in maxOf(now.minusSeconds(TRACKED_MINUTES * 60L), candlesticks.values.first().openTimestamp).epochSecond / 60 until now.epochSecond / 60) {
            expectedHistory.add(candlesticks.values.last())
        }

        candlestickHistory shouldBe expectedHistory
    }

    @Test
    fun `returns candlesticks history when last candlestick is after limit`() = runBlocking {
        val now = Instant.now()
        val candlesticks = mutableMapOf(
            1 to candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.8).toLong())),
            2 to candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.5).toLong())),
            3 to candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.4).toLong())),
            4 to candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 1.3).toLong())),
            5 to candlestick(now.minusSeconds((TRACKED_MINUTES * 60 * 0.8).toLong())),
        )
        every { storage.getCandlesticks(any()) } returns candlesticks

        val candlestickHistory = service.getCandlesticks("test")

        val expectedHistory = mutableListOf<Candlestick>()

        for (minute: Int in (maxOf(now.minusSeconds(TRACKED_MINUTES * 60L), candlesticks.values.first().openTimestamp).epochSecond / 60).toInt() until (now.epochSecond / 60).toInt()) {
            expectedHistory.add(candlesticks.values.last())
        }

        candlestickHistory shouldBe expectedHistory
    }
}