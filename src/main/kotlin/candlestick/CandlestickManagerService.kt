package candlestick

import TRACKED_MINUTES
import persistence.CandlestickStorage
import quote.NonExistingInstrumentException
import java.time.Instant
import java.time.temporal.ChronoUnit

class CandlestickManagerService(val storage: CandlestickStorage) : CandlestickManager {
    override suspend fun getCandlesticks(isin: String): List<Candlestick> {
        val now = Instant.now()

        val sortedCandlesticks = storage.getCandlesticks(isin)?.values?.sortedBy { it.openTimestamp } ?: throw NonExistingInstrumentException()

        val candlesticksIterator = sortedCandlesticks.iterator()
        val historyMinutes = sortedCandlesticks.getHistoryMinutes(now)
        val historicCandlesticks = mutableListOf<Candlestick>()
        var lastCandlestick = sortedCandlesticks.getFirstValidCandlestick(now)

        for (minute: Long in historyMinutes downTo 0) {
            lastCandlestick = if (candlesticksIterator.hasNext() && now.minusSeconds(minute * 60) > lastCandlestick.openTimestamp)
                candlesticksIterator.next()
            else
                lastCandlestick.copyPreviousCandlestick()

            historicCandlesticks.add(lastCandlestick)
        }

        return historicCandlesticks
    }

    fun List<Candlestick>.getHistoryMinutes(now: Instant) = minOf(TRACKED_MINUTES.toLong(), (now.epochSecond - this.first().openTimestamp.epochSecond) / 60)

    fun List<Candlestick>.getFirstValidCandlestick(now: Instant) = this.lastOrNull {
        it.openTimestamp <= now.minusSeconds(TRACKED_MINUTES * 60L).truncatedTo(ChronoUnit.MINUTES)
    } ?: this.first()

    fun Candlestick.copyPreviousCandlestick() = this.copy(
        openTimestamp = this.openTimestamp.plusSeconds(60),
        closeTimestamp = this.closeTimestamp.plusSeconds(60)
    )
}