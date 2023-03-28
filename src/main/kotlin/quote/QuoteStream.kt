package quote

import StreamEvent

data class QuoteEvent(val data: Quote): StreamEvent