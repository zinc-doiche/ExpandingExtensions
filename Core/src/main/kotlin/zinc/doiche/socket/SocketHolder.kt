package zinc.doiche.socket

import com.google.common.collect.Multimaps
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.launchAsync
import zinc.doiche.socket.`object`.Message
import zinc.doiche.socket.`object`.MessageListener
import zinc.doiche.socket.`object`.ProtocolType
import zinc.doiche.util.LoggerUtil
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.HashMap

abstract class SocketHolder(
    val manager: SocketManger
) {
    abstract val socket: ABoundSocket

    lateinit var readChannel: ByteReadChannel
        protected set
    lateinit var writeChannel: ByteWriteChannel
        protected set

    abstract suspend fun connect()

    open suspend fun close() {
        readChannel.cancel()
        writeChannel.close()
    }
}

class ServerSocketHolder(
    override val socket: ServerSocket,
    manager: SocketManger
) : SocketHolder(manager) {
    lateinit var acceptedSocket: Socket
        private set

    private val messageListeners = Multimaps.synchronizedListMultimap<ProtocolType, MessageListener>(
        Multimaps.newListMultimap(
            EnumMap(ProtocolType::class.java), { mutableListOf() }
        ) )

    override suspend fun connect() {

    }

    fun addListener(listener: MessageListener) {

    }

    override suspend fun close() {
        super.close()
        acceptedSocket.close()
        socket.close()
    }

    suspend fun await() {
        LoggerUtil.prefixedInfo("[TCP Server] Wait accepting...")
        acceptedSocket = socket.accept()
        LoggerUtil.prefixedInfo("[Server] Accepted!")
        readChannel = acceptedSocket.openReadChannel()
        writeChannel = acceptedSocket.openWriteChannel(autoFlush = true)
    }
}

class ClientSocketHolder(
    override val socket: Socket,
    manager: SocketManger
) : SocketHolder(manager) {
    @get:Synchronized
    private val messageQueue: ConcurrentLinkedQueue<Message> = ConcurrentLinkedQueue()

    override suspend fun connect() {
        readChannel = socket.openReadChannel()
        writeChannel = socket.openWriteChannel(autoFlush = true)

        writeChannel.writeStringUtf8("${manager.serverName}:greeting")

        plugin.launchAsync {
            launch(Dispatchers.IO) {

            }
        }

    }

    suspend fun enqueue(message: Message) {
        TODO("Not yet implemented")
    }

    override suspend fun close() {
        super.close()
        socket.close()
    }
}