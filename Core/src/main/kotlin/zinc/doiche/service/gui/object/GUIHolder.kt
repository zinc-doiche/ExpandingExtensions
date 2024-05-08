package zinc.doiche.service.gui.`object`

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.InventoryHolder

interface GUIHolder: InventoryHolder {
    fun open(player: Player)
    fun onEvent(inventoryEvent: InventoryEvent, eventType: EventType)
}

interface ClickableHolder: GUIHolder {
    fun onClick(inventoryEvent: InventoryEvent)

    override fun onEvent(inventoryEvent: InventoryEvent, eventType: EventType) {
        if (eventType == EventType.CLICK) {
            onClick(inventoryEvent)
        }
    }
}

enum class EventType {
    CLICK,
    DRAG,
    CLOSE,
    OPEN,
    UPDATE
}