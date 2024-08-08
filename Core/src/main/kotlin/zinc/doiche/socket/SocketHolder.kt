package zinc.doiche.socket

import com.google.common.collect.Multimaps
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.socket.context.ClientContextManager
import zinc.doiche.socket.message.Message
import zinc.doiche.socket.message.MessageListener
import zinc.doiche.socket.message.MessageProtocol
import zinc.doiche.socket.message.MessageType
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

abstract class SocketHolder(
    val manager: SocketManger
) {
    val clientContextManager: ClientContextManager = ClientContextManager(manager)

    @get:Synchronized
    protected val messageQueue: ConcurrentLinkedQueue<Message> = ConcurrentLinkedQueue()

    protected val messageServerListeners = Multimaps
        .newListMultimap<MessageProtocol, MessageListener>(
            EnumMap(MessageProtocol::class.java),
            ::mutableListOf
        ).let {
            Multimaps.synchronizedListMultimap(it)
        }

    lateinit var readChannel: ByteReadChannel
        protected set
    lateinit var writeChannel: ByteWriteChannel
        protected set

    fun addListener(listener: MessageListener) {
        messageServerListeners.put(listener.messageProtocol, listener)
    }

    suspend fun enqueue(message: Message) {
        messageQueue.add(message)
    }

    protected suspend fun launchMessageHandlers(socket: Socket) {
        coroutineScope {
            launch(Dispatchers.IO) {
                while (!socket.isClosed) {
                    messageQueue.peek()?.let { message ->
                        clientContextManager.bindRequest(message)
                        writeChannel.writeStringUtf8(message.protocolize())
                    }
                }
            }
            launch(Dispatchers.IO) {
                while (!socket.isClosed) {
                    val line = readChannel.readUTF8Line() ?: continue

                    Message.parse(line)?.let { message ->
                        when (message.messageType) {
                            MessageType.RESPONSE -> {
                                clientContextManager.handleResponse(message)
                            }

                            MessageType.REQUEST -> {
                                messageServerListeners[message.messageProtocol].forEach { listener ->
                                    listener.onMessage(message)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    open suspend fun close() {
        readChannel.cancel()
        writeChannel.close()
    }
}

class ServerSocketHolder(
    val socket: ServerSocket,
    manager: SocketManger
) : SocketHolder(manager) {
    lateinit var acceptedSocket: Socket
        private set

    suspend fun await() {
        coroutineScope {
            plugin.slF4JLogger.info("[TCP Server] Wait accepting...")

            acceptedSocket = socket.accept()

            plugin.slF4JLogger.info("[TCP Server] Accepted!")

            readChannel = acceptedSocket.openReadChannel()
            writeChannel = acceptedSocket.openWriteChannel(autoFlush = true)

            readChannel.readUTF8Line()?.let {
                Message.parse(it)?.let { message ->
                    plugin.slF4JLogger.info(message.body)

                    writeChannel.writeStringUtf8(MessageProtocol.HANDSHAKE.message("greetings!"))
                }
            } ?: throw IOException("클라이언트의 요청이 없습니다.")

            launchMessageHandlers(acceptedSocket)
        }
    }

    override suspend fun close() {
        super.close()
        acceptedSocket.close()
        socket.close()
    }
}

class ClientSocketHolder(
    socket: Socket,
    manager: SocketManger
) : SocketHolder(manager) {
    var socket: Socket = socket
        private set

    suspend fun connect() {
        coroutineScope {
//            while ()
            readChannel = socket.openReadChannel()
            writeChannel = socket.openWriteChannel(autoFlush = true)

            writeChannel.writeStringUtf8(MessageProtocol.HANDSHAKE.message("greetings!"))

            readChannel.readUTF8Line()?.let {
                Message.parse(it)?.let { message ->
                    plugin.slF4JLogger.info(message.body)
                } ?: return@let null
            } ?: throw IOException("서버의 응답이 없습니다.")

            launchMessageHandlers(socket)
        }
    }

    override suspend fun close() {
        super.close()
        socket.close()
    }
}