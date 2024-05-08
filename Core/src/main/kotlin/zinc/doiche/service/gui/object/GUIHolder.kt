package zinc.doiche.service.gui.`object`

import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.InventoryHolder
import java.util.*

interface GUIHolder: InventoryHolder {
    val guiName: String
    val uuid: UUID

    fun open()
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