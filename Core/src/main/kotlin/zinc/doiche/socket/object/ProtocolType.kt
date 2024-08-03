package zinc.doiche.socket.`object`

enum class ProtocolType {
    HANDSHAKE,
    ;

    companion object {
        fun fromNumber(value: Int): ProtocolType {
            return entries[value]
        }
    }
}