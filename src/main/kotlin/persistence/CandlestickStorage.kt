package persistence

import candlestick.Candlestick
import instrument.ISIN
import instrument.Instrument

interface CandlestickStorage {
    fun getCandlesticks(isin: ISIN): MutableMap<Int, Candlestick>?
    fun storeInstrument(instrument: Instrument): Boolean
    fun deleteInstrument(isin: ISIN): Boolean
    fun storeCandlesticks(isin: ISIN, candlesticks: MutableMap<Int, Candlestick>): Boolean
}
