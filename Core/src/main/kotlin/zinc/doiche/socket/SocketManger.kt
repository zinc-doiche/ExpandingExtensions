package zinc.doiche.socket;

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mojang.brigadier.Command
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import kotlinx.coroutines.*
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.executesPlayer
import zinc.doiche.lib.launchAsync
import zinc.doiche.lib.requiresOp
import kotlin.time.Duration.Companion.seconds

class SocketManger(
    val serverName: String,
    val servers: Map<String, ServerInfo>
) {
    private val socketMap = mutableMapOf<String, SocketHolder>()

    fun self() = socketMap[serverName]

    suspend fun connect() {
//        val selectorManager = SelectorManager(Dispatchers.IO)
//
//        withTimeout(10.seconds) {
//            servers.forEach { (name, info) ->
//                val socketHolder = aSocket(selectorManager).tcp().let {
//                    if(name == serverName) {
//                        it.bind(info.host, info.port).let { socket ->
//                            plugin.slF4JLogger.info("[$name] ${info.host}:${info.port} 바인딩")
//                            ServerSocketHolder(socket)
//                        }
//                    } else {
//                        it.connect(info.host, info.port).let { socket ->
//                            plugin.slF4JLogger.info("[$name] ${info.host}:${info.port} 연결됨")
//                            ClientSocketHolder(socket)
//                        }
//                    }
//                }
//                socketHolder.init()
//                socketMap[name] = socketHolder
//            }
//            plugin.slF4JLogger.info("[$serverName] 소켓 모두 연결됨")
//        }
//
//        plugin.launchAsync {
//            while (true) {
//                self()?.let {
//                    it.readChannel.readUTF8Line()?.let { line ->
//                        plugin.slF4JLogger.info(line)
//                    } ?: plugin.slF4JLogger.info(" no line ")
//                }
//            }
//        }

        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS.newHandler { event ->
            Commands.literal("sendSth")
                .requiresOp()
                .executesPlayer { commandContext, player ->
                    plugin.launchAsync {
                        val selectorManager = SelectorManager(Dispatchers.IO)
                        val socket = aSocket(selectorManager).tcp().connect("127.0.0.1", 9002)
                        val receiveChannel = socket.openReadChannel()
                        val sendChannel = socket.openWriteChannel(autoFlush = true)

                        launch(Dispatchers.IO) {
                            while (true) {
                                val greeting = receiveChannel.readUTF8Line()
                                if (greeting != null) {
                                    player.sendMessage(greeting)
                                } else {
                                    player.sendMessage("Server closed a connection")
                                    socket.close()
                                    selectorManager.close()
                                    return@launch
                                }
                            }
                        }
                        while (true) {
                            val myMessage = readln()
                            sendChannel.writeStringUtf8("$myMessage\n")
                        }
                    }
                    Command.SINGLE_SUCCESS
                }
                .build()
                .let { event.registrar().register(it) }

            Commands.literal("connect")
                .requiresOp()
                .executesPlayer { commandContext, player ->
                    plugin.launchAsync {
                        val selectorManager = SelectorManager(Dispatchers.IO)
                        val socket = aSocket(selectorManager).tcp().bind("localhost", 1080)
                        player.sendMessage("Server is listening at ${socket.localAddress}")
                        while (true) {
                            val socket = socket.accept()
                            player.sendMessage("Accepted $socket")
                            launch {
                                val receiveChannel = socket.openReadChannel()
                                val sendChannel = socket.openWriteChannel(autoFlush = true)
                                sendChannel.writeStringUtf8("Please enter your name\n")
                                try {
                                    while (true) {
                                        val name = receiveChannel.readUTF8Line()
                                        sendChannel.writeStringUtf8("Hello, $name!\n")
                                    }
                                } catch (e: Throwable) {
                                    socket.close()
                                }
                            }
                        }
                    }
                    Command.SINGLE_SUCCESS
                }
                .build()
                .let { event.registrar().register(it) }
        })
    }

    fun close() {
        runBlocking(Dispatchers.IO) {
            socketMap.values.forEach { it.close() }
        }
    }
}
