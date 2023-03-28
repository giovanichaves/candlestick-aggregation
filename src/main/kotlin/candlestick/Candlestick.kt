package candlestick

import quote.Price
import java.time.Instant

interface CandlestickManager {
    suspend fun getCandlesticks(isin: String): List<Candlestick>
}

data class Candlestick(
    val openTimestamp: Instant,
    var closeTimestamp: Instant,
    val openPrice: Price,
    var highPrice: Price,
    var lowPrice: Price,
    var closingPrice: Price
)