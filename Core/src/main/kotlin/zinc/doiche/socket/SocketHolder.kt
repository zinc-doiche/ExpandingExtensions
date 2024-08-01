package zinc.doiche.socket

import io.ktor.network.sockets.*
import io.ktor.utils.io.*

interface SocketHolder {
    val socket: ABoundSocket
    var readChannel: ByteReadChannel
    var writeChannel: ByteWriteChannel

    suspend fun init()

    suspend fun send() {
        writeChannel.writeStringUtf8("Hello World!")
    }

    suspend fun close() {
        readChannel.cancel()
        writeChannel.close()
    }
}

class ServerSocketHolder(
    override val socket: ServerSocket
) : SocketHolder {
    override lateinit var readChannel: ByteReadChannel
    override lateinit var writeChannel: ByteWriteChannel

    override suspend fun init() {
        val socket = socket.accept()
        readChannel = socket.openReadChannel()
        writeChannel = socket.openWriteChannel(autoFlush = true)
    }

    override suspend fun close() {
        super.close()
        socket.close()
    }
}

class ClientSocketHolder(
    override val socket: Socket
) : SocketHolder {
    override lateinit var readChannel: ByteReadChannel
    override lateinit var writeChannel: ByteWriteChannel

    override suspend fun init() {
        readChannel = socket.openReadChannel()
        writeChannel = socket.openWriteChannel(autoFlush = true)
    }

    override suspend fun close() {
        super.close()
        socket.close()
    }
}