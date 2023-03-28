package instrument

data class Instrument(val isin: ISIN, val description: String)

typealias ISIN = String
