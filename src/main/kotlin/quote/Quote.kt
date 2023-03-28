package quote

import instrument.ISIN

data class Quote(val isin: ISIN, val price: Price)

typealias Price = Double
