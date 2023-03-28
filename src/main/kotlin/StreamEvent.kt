interface StreamEvent {

}

//class Stream(
//    uriString: String = "ws://localhost:$WSPORT/instruments",
//) {
//    val logger = LoggerFactory.getLogger(javaClass)
//
//    val uri = Uri.of(uriString)
//
//    lateinit var ws: Websocket
//
//    inline fun <reified T: StreamEvent> connect(crossinline onEvent: (T) -> Unit) {
//        ws = WebsocketClient.nonBlocking(uri) { logger.info("Connected instrument stream") }
//
//        ws.onMessage {
//            val event: T = jackson.readValue<T>(it.body.stream)
//            onEvent(event)
//        }
//
//        ws.onClose {
//            logger.info("Disconnected instrument stream: ${it.code}; ${it.description}")
//            runBlocking {
//                launch {
//                    delay(5000L)
//                    logger.info("Attempting reconnect for instrument stream")
//                    connect(onEvent)
//                }
//            }
//        }
//
//        ws.onError {
//            logger.info("Exception in instrument stream: $it")
//        }
//    }
//}

