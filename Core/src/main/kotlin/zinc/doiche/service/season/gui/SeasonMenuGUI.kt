package zinc.doiche.service.season.gui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import zinc.doiche.service.gui.GUIService
import zinc.doiche.service.gui.entity.GUI
import zinc.doiche.service.gui.`object`.EventType
import zinc.doiche.service.gui.`object`.GUIHolder

class SeasonMenuGUI : GUIHolder {
    companion object {
        private const val GUI_NAME = "season_menu"
    }

    override val gui: GUI?
        get() = GUIService.repository.findCacheByName(GUI_NAME)

    private val inventory: Inventory = Bukkit.createInventory(this, 54)

    override fun getInventory(): Inventory = inventory

    override fun open(player: Player) {
        gui?.let {
           // player.openGUI(it, inventory)
        }
    }

    override fun onEvent(inventoryEvent: InventoryEvent, eventType: EventType) {
        TODO("Not yet implemented")
    }

}