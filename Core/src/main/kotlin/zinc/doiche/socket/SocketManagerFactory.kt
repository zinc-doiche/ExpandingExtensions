package zinc.doiche.socket

import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.util.toObject

private const val CONFIG_PATH = "ktor/config.json"

class SocketManagerFactory {
    fun create(): SocketManger = plugin.config(CONFIG_PATH)
        .toObject(ServerConfig::class.java)
        .let {
            SocketManger(it.name, it.servers)
        }
}

data class ServerInfo(
    val name: String,
    val port: Int,
    val host: String
)

data class ServerConfig(
    val name: String,
    val servers: Map<String, ServerInfo>
)

