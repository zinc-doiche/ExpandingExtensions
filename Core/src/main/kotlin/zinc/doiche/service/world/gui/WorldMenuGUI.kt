package zinc.doiche.service.world.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import zinc.doiche.service.gui.entity.GUI
import zinc.doiche.service.gui.`object`.ClickableHolder

class WorldMenuGUI(
    override val gui: GUI
) : ClickableHolder {
    //TODO
    private lateinit var inventoryView: InventoryView

    override fun onClick(event: InventoryEvent) {

    }

    override fun open(player: Player) {
        inventoryView = gui.openInventoryView(player, this) ?: return
    }

    override fun getInventory(): Inventory {
        //TODO
        return inventoryView.topInventory
    }
}