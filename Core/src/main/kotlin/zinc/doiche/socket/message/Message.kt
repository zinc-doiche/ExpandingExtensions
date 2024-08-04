package zinc.doiche.socket.message

import java.util.concurrent.atomic.AtomicInteger

data class Message(
    val messageType: MessageType,
    val messageProtocol: MessageProtocol,
    val contextId: Int,
    val body: String,

    @Transient
    val onSuccess: ((Message) -> Unit)? = null,
) {
    constructor(messageType: MessageType, messageProtocol: MessageProtocol, body: String) : this(
        messageType,
        messageProtocol,
        getNextId(),
        body
    )

    constructor(
        messageType: MessageType,
        messageProtocol: MessageProtocol,
        body: String,
        onSuccess: (Message) -> Unit
    ) : this(
        messageType,
        messageProtocol,
        getNextId(),
        body,
        onSuccess
    )

    fun protocolize(): String = "${messageType.ordinal}:${messageProtocol.ordinal}:$body"

    companion object {
        private const val CONTEXT_ID_PERIOD: Int = 100
        private const val MESSAGE_CONTENT_SIZE: Int = 4
        private var nextId: AtomicInteger = AtomicInteger()

        fun parse(line: String): Message? = line.split(':', limit = MESSAGE_CONTENT_SIZE)
            .takeIf { it.size == MESSAGE_CONTENT_SIZE }
            ?.let {
                val type = MessageType.fromNumber(it[0].toInt())
                val protocol = MessageProtocol.fromNumber(it[1].toInt())
                val id = it[2].toInt()

                Message(type, protocol, id, it[3])
            }

        private fun getNextId(): Int = nextId.getAndIncrement().let {
            if(it < CONTEXT_ID_PERIOD - 1) {
                it
            } else {
                nextId.updateAndGet { value -> value - CONTEXT_ID_PERIOD }
            }
        }
    }
}
