package instrument

import StreamEvent

data class InstrumentEvent(val type: Type, val data: Instrument): StreamEvent {
    enum class Type {
        ADD,
        DELETE
    }
}