package instrument

import persistence.CandlestickStorage

class InstrumentService(
    val storage: CandlestickStorage
) {

    fun registerInstrumentEvent(event: InstrumentEvent) {
        when (event.type) {
            InstrumentEvent.Type.ADD -> addInstrument(event.data)
            InstrumentEvent.Type.DELETE -> deleteInstrument(event.data)
        }
    }

    fun addInstrument(instrument: Instrument) = storage.storeInstrument(instrument)

    fun deleteInstrument(instrument: Instrument) = storage.deleteInstrument(instrument.isin)

}
