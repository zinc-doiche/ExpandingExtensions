package zinc.doiche.socket;

import com.mojang.brigadier.Command
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.event.player.AsyncChatEvent
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.executesPlayer
import zinc.doiche.lib.launchAsync
import zinc.doiche.lib.requiresOp

class SocketManger(
    val serverName: String,
    val servers: Map<String, ServerInfo>
) {
    private val socketMap = mutableMapOf<String, SocketHolder>()

    fun self() = socketMap[serverName]

    suspend fun connect() {
        plugin.launchAsync {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 1080)

            Bukkit.broadcast(text("[Server] Server is listening at ${serverSocket.localAddress}"))

            launch(Dispatchers.IO) {
                while (true) {
                    Bukkit.broadcast(text("[Server] Wait accepting..."))
                    val socket = serverSocket.accept()
                    Bukkit.broadcast(text("[Server] Accepted!"))
                    val receiveChannel = socket.openReadChannel()
                    val sendChannel = socket.openWriteChannel(autoFlush = true)
                    Bukkit.broadcast(text("[Server] Wait writing..."))
                    sendChannel.writeStringUtf8("greetings!\n")
                    Bukkit.broadcast(text("[Server] Writing End!"))
                    try {
                        while (true) {
                            Bukkit.broadcast(text("[Server] Waiting read..."))
                            val message = receiveChannel.readUTF8Line()
                            sendChannel.writeStringUtf8("Your message: $message\n")
                            Bukkit.broadcast(text("[Server] read end!"))
                        }
                    } catch (e: Throwable) {
                        socket.close()
                    }
                }
            }
        }

        lateinit var receiveChannel: ByteReadChannel
        lateinit var sendChannel: ByteWriteChannel
        lateinit var socket: Socket

        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS.newHandler { event ->
            Commands.literal("connect")
                .requiresOp()
                .executesPlayer { commandContext, player ->
                    plugin.launchAsync {
                        val selectorManager = SelectorManager(Dispatchers.IO)
                        Bukkit.broadcast(text("[Client] Wait connection..."))
                        socket = aSocket(selectorManager).tcp().connect("127.0.0.1", 1080)
                        Bukkit.broadcast(text("[Client] Connected!"))
                        receiveChannel = socket.openReadChannel()
                        sendChannel = socket.openWriteChannel(autoFlush = true)

                        val message = receiveChannel.readUTF8Line() ?: "No message"

                        player.sendMessage(message)
                    }
                    Command.SINGLE_SUCCESS
                }
                .build()
                .let { event.registrar().register(it) }
        })

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

    fun close() {
        runBlocking(Dispatchers.IO) {
            socketMap.values.forEach { it.close() }
        }
    }
}
