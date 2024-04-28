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
import zinc.doiche.service.user.UserService
import zinc.doiche.service.user.`object`.QUser.user
import zinc.doiche.service.user.repository.UserRepository
import zinc.doiche.service.user.user

@ListenerRegistry
class CommonListener: Listener {
    @EventHandler
    fun onReload(event: ServerLoadEvent) {
        if(event.type == ServerLoadEvent.LoadType.RELOAD) {
            plugin.server.broadcast(prefixed("Reload User Data..."))

            val uuidList = Bukkit.getOnlinePlayers().map { it.uniqueId }
            val idMap = plugin.query
                .select(user.id, user.uuid)
                .from(user)
                .where(user.uuid.`in`(uuidList))
                .fetch()
            idMap.forEach { tuple ->
                val id = tuple.get(user.id)
                val uuid = tuple.get(user.uuid)
                UserService.repository.saveID(uuid!!, id!!)
            }
        }
    }
}
