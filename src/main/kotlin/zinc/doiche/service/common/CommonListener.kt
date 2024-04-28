package zinc.doiche.service.common;

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import zinc.doiche.Main
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.annotation.ListenerRegistry
import zinc.doiche.lib.log.LoggerUtil
import zinc.doiche.lib.log.LoggerUtil.prefixed

@ListenerRegistry
class CommonListener: Listener {
    @EventHandler
    fun onReload(event: ServerLoadEvent) {
//        if(event.type == ServerLoadEvent.LoadType.RELOAD) {
//            plugin.server.broadcast(prefixed("Reload User Data..."))
//            Bukkit.getOnlinePlayers().forEach { player ->
//
//            }
//        }
    }
}
