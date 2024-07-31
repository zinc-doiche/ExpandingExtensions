package zinc.doiche.service.worldgen.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldInitEvent
import zinc.doiche.lib.ListenerRegistry

@ListenerRegistry
class WorldGenListener: Listener {
    @EventHandler
    fun onWorldInit(event: WorldInitEvent) {
//        event.world.generator.getDefaultPopulators(event.world).clear(
    }

}