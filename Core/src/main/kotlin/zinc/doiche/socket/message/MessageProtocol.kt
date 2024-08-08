package zinc.doiche.socket.message

import zinc.doiche.util.serialize

enum class MessageProtocol {
    HANDSHAKE,
    ;

    fun message(body: Any): String = "$ordinal:${body.serialize()}"

    companion object {
        fun fromNumber(value: Int): MessageProtocol {
            return entries[value]
        }
    }
}

