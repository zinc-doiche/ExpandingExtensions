package zinc.doiche.socket

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit

abstract class SocketHolder {
    abstract val socket: ABoundSocket

    lateinit var readChannel: ByteReadChannel
        protected set
    lateinit var writeChannel: ByteWriteChannel
        protected set

    abstract suspend fun connect()
    abstract suspend fun send()

    open suspend fun close() {
        readChannel.cancel()
        writeChannel.close()
    }
}

class ServerSocketHolder(
    override val socket: ServerSocket
) : SocketHolder() {
    lateinit var acceptedSocket: Socket
        private set

    override suspend fun connect() {
        while (!socket.isClosed) {
            Bukkit.broadcast(text("[Server] Wait accepting..."))
            acceptedSocket = socket.accept()
            Bukkit.broadcast(text("[Server] Accepted!"))
            readChannel = acceptedSocket.openReadChannel()
            writeChannel = acceptedSocket.openWriteChannel(autoFlush = true)

            while (!acceptedSocket.isClosed) {
                Bukkit.broadcast(text("[Server] Waiting read..."))
                val message = readChannel.readUTF8Line()
                writeChannel.writeStringUtf8("Your message: $message\n")
                Bukkit.broadcast(text("[Server] read end!"))
            }
        }
    }

    override suspend fun send() {
        TODO("Not yet implemented")
    }

    override suspend fun close() {
        super.close()
        acceptedSocket.close()
        socket.close()
    }
}

class ClientSocketHolder(
    override val socket: Socket
) : SocketHolder() {
    override suspend fun connect() {
        readChannel = socket.openReadChannel()
        writeChannel = socket.openWriteChannel(autoFlush = true)
    }

    override suspend fun send() {
        TODO("Not yet implemented")
    }

    override suspend fun close() {
        super.close()
        socket.close()
    }
}