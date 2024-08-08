package zinc.doiche.socket.context

import com.google.common.collect.Table
import com.google.common.collect.Tables
import zinc.doiche.socket.SocketManger
import zinc.doiche.socket.message.Message
import zinc.doiche.socket.message.MessageProtocol
import java.util.EnumMap
import java.util.TreeMap

class ClientContextManager(
    val manager: SocketManger
) {
    // Request 후 해당 Message의 식별자가 key인 값이 이 테이블에 저장되는 것을 await
    private val responseHandlerTable: Table<MessageProtocol, Int, Message> = Tables
        .newCustomTable(
            EnumMap(MessageProtocol::class.java),
            ::TreeMap
        )

    fun bindRequest(message: Message) {
        val messageProtocol = message.messageProtocol
        val contextId = message.contextId

        responseHandlerTable.put(messageProtocol, contextId, message)
    }

    fun handleResponse(response: Message) {
        responseHandlerTable.get(response.messageProtocol, response.contextId)?.let { request ->
            request.onSuccess?.let { it(response) }
        }
    }
}