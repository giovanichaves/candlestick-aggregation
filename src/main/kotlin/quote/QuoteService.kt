package quote

import candlestick.Candlestick
import persistence.CandlestickStorage
import trackedSlot
import java.time.Instant
import java.time.temporal.ChronoUnit

class QuoteService(
    val storage: CandlestickStorage
) {
    fun registerQuoteEvent(quoteEvent: QuoteEvent) {
        val now = Instant.now()
        val currentSlot = now.trackedSlot()
        val candlesticks = storage.getCandlesticks(quoteEvent.data.isin) ?: throw NonExistingInstrumentException()

        candlesticks.compute(currentSlot) { _, candlestick -> candlestick?.addQuote(now, quoteEvent.data) ?: setQuote(now, quoteEvent.data) }
        storage.storeCandlesticks(quoteEvent.data.isin, candlesticks)
    }

    fun Candlestick.addQuote(now: Instant, quote: Quote): Candlestick {
        if (this.closeTimestamp.isBefore(now)) return setQuote(now, quote)

        if (quote.price > this.highPrice) this.highPrice = quote.price
        if (quote.price < this.lowPrice) this.lowPrice = quote.price
        this.closingPrice = quote.price

        return this
    }

    fun setQuote(now: Instant, quote: Quote) = Candlestick(
        openTimestamp = now.truncatedTo(ChronoUnit.MINUTES),
        closeTimestamp = now.plusSeconds(60).truncatedTo(ChronoUnit.MINUTES),
        openPrice = quote.price,
        highPrice = quote.price,
        lowPrice = quote.price,
        closingPrice = quote.price
    )
}