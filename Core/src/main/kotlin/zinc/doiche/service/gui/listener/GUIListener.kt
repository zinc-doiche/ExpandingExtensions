package zinc.doiche.service.gui.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import zinc.doiche.lib.ListenerRegistry
import zinc.doiche.service.gui.`object`.EventType
import zinc.doiche.service.gui.`object`.GUIHolder

@ListenerRegistry(async = true)
class GUIListener: Listener {
    @EventHandler
    suspend fun onClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder as? GUIHolder ?: return
        holder.onEvent(event, EventType.CLICK)
    }

    @EventHandler
    suspend fun onDrag(event: InventoryClickEvent) {
        val holder = event.inventory.holder as? GUIHolder ?: return
        holder.onEvent(event, EventType.DRAG)
    }

    @EventHandler
    suspend fun onClose(event: InventoryClickEvent) {
        val holder = event.inventory.holder as? GUIHolder ?: return
        holder.onEvent(event, EventType.CLOSE)
    }

    @EventHandler
    suspend fun onOpen(event: InventoryClickEvent) {
        val holder = event.inventory.holder as? GUIHolder ?: return
        holder.onEvent(event, EventType.OPEN)
    }

    @EventHandler
    suspend fun onUpdate(event: InventoryClickEvent) {
        val holder = event.inventory.holder as? GUIHolder ?: return
        holder.onEvent(event, EventType.UPDATE)
    }
}