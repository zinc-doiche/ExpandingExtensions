package zinc.doiche.socket;

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.launchAsync
import zinc.doiche.util.LoggerUtil
import java.io.Closeable

class SocketManger(
    val serverName: String,
    val servers: Map<String, ServerInfo>
) : Closeable {
    private val socketMap = mutableMapOf<String, SocketHolder>()

    fun self() = socketMap[serverName]

    suspend fun connect() {
        plugin.launchAsync {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val serverInfo = servers[serverName] ?: error("Server $serverName not found")
            val serverSocket = aSocket(selectorManager).tcp().bind(serverInfo.address)

            LoggerUtil.prefixedInfo("[TCP Server] Listening at: ${serverSocket.localAddress}")

            ServerSocketHolder(serverSocket).let {
                socketMap[serverName] = it
                launch(Dispatchers.IO) {
                    it.connect()
                }
            }

            servers.forEach { (name, info) ->
                if (name == serverName) {
                    return@forEach
                }
                val socket = aSocket(selectorManager).tcp().connect(info.address)

                LoggerUtil.prefixedInfo("[Client] Connected with: $name")

                ClientSocketHolder(socket).let {
                    socketMap[name] = it
                    launch(Dispatchers.IO) {
                        it.connect()
                    }
                }
            }
        }

        lateinit var receiveChannel: ByteReadChannel
        lateinit var sendChannel: ByteWriteChannel

        plugin.server.pluginManager.registerEvents(object : Listener {
            @EventHandler
            fun onChat(event: AsyncChatEvent) {
                val player = event.player
                val message = event.message() as TextComponent

                plugin.launchAsync {
                    launch(Dispatchers.IO) {
                        Bukkit.broadcast(text("[Client] Wait Writing..."))
                        sendChannel.writeStringUtf8("${message.content()}\n")
                        Bukkit.broadcast(text("[Client] writing end!"))

                        Bukkit.broadcast(text("[Client] Wait Reading..."))
                        val response = receiveChannel.readUTF8Line() ?: "no response"
                        Bukkit.broadcast(text("[Client] read end!"))

                        player.sendMessage(text(response))
                    }
                }
            }
        }, plugin)
    }

    override fun close() {
        runBlocking(Dispatchers.IO) {
            socketMap.values.forEach { it.close() }
        }
    }
}
