package zinc.doiche.socket;

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component.text
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.launchAsync
import zinc.doiche.util.LoggerUtil
import java.io.Closeable

class SocketManger(
    val serverName: String,
    val servers: Map<String, ServerInfo>
) : Closeable {
    private val socketMap = mutableMapOf<String, SocketHolder>()

    suspend fun connect() {
        plugin.launchAsync {
            launch(Dispatchers.IO) {
                val selectorManager = SelectorManager(Dispatchers.IO)

                servers.forEach { (name, info) ->
                    if (name == serverName) {
                        return@forEach
                    }
                    launch(Dispatchers.IO) {
                        runCatching {
                            aSocket(selectorManager)
                                .tcpNoDelay()
                                .tcp()
                                .connect(info.address)
                        }.onSuccess { socket ->
                            val holder = ClientSocketHolder(socket, this@SocketManger)

                            LoggerUtil.prefixedInfo("[TCP Client] Connected with: $name")

                            socketMap[name] = holder
                            holder.connect()
                        }.onFailure {
                            LoggerUtil.prefixedInfo(
                                text("[TCP Client] Connection failed with: $name, Trying to binding...")
                            )
                            aSocket(selectorManager)
                                .tcpNoDelay()
                                .tcp()
                                .bind(info.address)
                                .let { serverSocket ->
                                    val holder = ServerSocketHolder(serverSocket, this@SocketManger)

                                    LoggerUtil.prefixedInfo("[TCP Server] Listening at: ${serverSocket.localAddress}")

                                    socketMap[name] = holder
                                    holder.await()
                                }
                        }
                    }
                }
            }
        }
//
//        lateinit var receiveChannel: ByteReadChannel
//        lateinit var sendChannel: ByteWriteChannel
//
//        plugin.server.pluginManager.registerEvents(object : Listener {
//            @EventHandler
//            fun onChat(event: AsyncChatEvent) {
//                val player = event.player
//                val message = event.message() as TextComponent
//
//                plugin.launchAsync {
//                    launch(Dispatchers.IO) {
//                        Bukkit.broadcast(text("[Client] Wait Writing..."))
//                        sendChannel.writeStringUtf8("${message.content()}\n")
//                        Bukkit.broadcast(text("[Client] writing end!"))
//
//                        Bukkit.broadcast(text("[Client] Wait Reading..."))
//                        val response = receiveChannel.readUTF8Line() ?: "no response"
//                        Bukkit.broadcast(text("[Client] read end!"))
//
//                        player.sendMessage(text(response))
//                    }
//                }
//            }
//        }, plugin)
    }

    override fun close() {
        runBlocking(Dispatchers.IO) {
            socketMap.values.forEach { it.close() }
        }
    }
}
