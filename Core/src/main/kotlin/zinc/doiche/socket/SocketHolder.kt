package zinc.doiche.socket

import com.google.common.collect.Multimaps
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.launchAsync
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

    protected suspend fun launchMessageHandlers(socket: Socket) = coroutineScope {
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

    abstract suspend fun onDisconnect()

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
            var handlerJob: Job? = null

            plugin.slF4JLogger.info("[TCP Server] 클라이언트의 연결 대기 중...")

            acceptedSocket = socket.accept()

            plugin.slF4JLogger.info("[TCP Server] 연결되었습니다.")

            readChannel = acceptedSocket.openReadChannel()
            writeChannel = acceptedSocket.openWriteChannel(autoFlush = true)

            readChannel.readUTF8Line()?.let {
                Message.parse(it)?.let { message ->
                    plugin.slF4JLogger.info(message.body)

                    writeChannel.writeStringUtf8(MessageProtocol.HANDSHAKE.message("greetings!"))
                }
            } ?: onDisconnect()

            handlerJob = launchMessageHandlers(acceptedSocket)
        }
    }

    override suspend fun onDisconnect() {
        plugin.slF4JLogger.warn("[TCP Server] 클라이언트의 응답이 없습니다. 다시 연결될 때까지 대기합니다.")

        super.close()
        if(!acceptedSocket.isClosed) {
            acceptedSocket.close()
        }
        launchAwait()
    }

    private fun launchAwait() {
        plugin.launchAsync {
            launch(Dispatchers.IO) {
                await()
            }
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
            readChannel = socket.openReadChannel()
            writeChannel = socket.openWriteChannel(autoFlush = true)

            writeChannel.writeStringUtf8(MessageProtocol.HANDSHAKE.message("greetings!"))

            readChannel.readUTF8Line()?.let {
                Message.parse(it)?.let { message ->
                    plugin.slF4JLogger.info(message.body)
                } ?: return@let null
            } ?: onDisconnect()

            launchMessageHandlers(socket)
        }
    }

    override suspend fun onDisconnect() {
        plugin.slF4JLogger.warn("[TCP Client] 서버의 응답이 없습니다. 다시 연결될 때까지 대기합니다.")
        super.close()
        launchConnect()
    }

    private fun launchConnect() {
        plugin.launchAsync {
            launch(Dispatchers.IO) {
                connect()
            }
        }
    }

    override suspend fun close() {
        super.close()
        socket.close()
    }
}