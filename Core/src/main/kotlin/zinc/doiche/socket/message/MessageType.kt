package zinc.doiche.socket.message

enum class MessageType {
    REQUEST, RESPONSE
    ;

    companion object {
        fun fromNumber(value: Int): MessageType {
            return entries[value]
        }
    }
}