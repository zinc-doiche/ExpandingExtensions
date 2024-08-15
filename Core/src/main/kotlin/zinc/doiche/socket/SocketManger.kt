package zinc.doiche.socket

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.*
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.launchAsync
import java.io.Closeable

class SocketManger(
    val serverName: String,
    val responseTimeoutSeconds: Int,
    val servers: Map<String, ServerInfo>
) : Closeable {
    private val socketMap = mutableMapOf<String, SocketHolder>()

    constructor(config: ServerConfig) : this(
        config.name,
        config.responseTimeoutSeconds,
        config.servers
    )

    fun connect() {
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

                            plugin.slF4JLogger.info("[TCP Client] $name(와)과 연결 중...")

                            socketMap[name] = holder
                            holder.connect()
                        }.onFailure {
                            plugin.slF4JLogger.warn("[TCP Client] $name(와)과 연결하는 데 실패하였습니다. 호스트(Server)로서 바인딩합니다.")
                            aSocket(selectorManager)
                                .tcpNoDelay()
                                .tcp()
                                .bind(info.address)
                                .let { serverSocket ->
                                    val holder = ServerSocketHolder(serverSocket, this@SocketManger)

                                    plugin.slF4JLogger.info("[TCP Server] ${serverSocket.localAddress}에서 요청을 기다리는 중...")

                                    socketMap[name] = holder
                                    holder.await()
                                }
                        }
                    }
                }
            }
        }
    }

    override fun close() {
        runBlocking(Dispatchers.IO) {
            socketMap.values.forEach { it.close() }
        }
    }
}
