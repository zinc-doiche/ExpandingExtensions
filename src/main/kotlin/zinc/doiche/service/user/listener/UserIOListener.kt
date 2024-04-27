package zinc.doiche.service.user.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import zinc.doiche.lib.annotation.ListenerRegistry

@ListenerRegistry
class UserIOListener: Listener {
    @EventHandler
    fun onPreLogin(event: AsyncPlayerPreLoginEvent) {
        val uuid = event.uniqueId
    }
}