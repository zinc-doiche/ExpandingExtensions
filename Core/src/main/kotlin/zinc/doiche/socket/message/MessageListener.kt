package zinc.doiche.socket.message

interface MessageListener {
    val messageProtocol: MessageProtocol

    fun onMessage(message: Message)
}