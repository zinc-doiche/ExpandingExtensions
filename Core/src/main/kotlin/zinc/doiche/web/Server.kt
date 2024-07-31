package zinc.doiche.web

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.util.toObject
import zinc.doiche.web.routing.module

private const val CONFIG_PATH = "ktor/config.json"

class Server {
    fun runServer() = plugin.config(CONFIG_PATH)
            .toObject(KtorConfig::class.java)
            .let {
                embeddedServer(
                    Netty,
                    port = it.port,
                    host = it.host,
                    module = Application::module
                ).start(wait = true)
            }
}

data class KtorConfig(
    val port: Int,
    val host: String
)

