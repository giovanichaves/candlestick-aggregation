package persistence

import candlestick.Candlestick
import instrument.ISIN
import instrument.Instrument

class InMemory: CandlestickStorage {
    private val candlestickStorage: MutableMap<ISIN, MutableMap<Int, Candlestick>> = hashMapOf()

    override fun getCandlesticks(isin: ISIN) = candlestickStorage[isin]

    override fun storeInstrument(instrument: Instrument) = candlestickStorage.putIfAbsent(instrument.isin, hashMapOf()) == null

    override fun deleteInstrument(isin: ISIN) = candlestickStorage.remove(isin) != null

    override fun storeCandlesticks(isin: ISIN, candlesticks: MutableMap<Int, Candlestick>): Boolean {
        candlestickStorage[isin] = candlesticks
        return true
    }
}