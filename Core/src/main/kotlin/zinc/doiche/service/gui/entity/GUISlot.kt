package zinc.doiche.service.gui.entity

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import zinc.doiche.lib.embeddable.DisplayedInfo

class GUISlot(
    val displayedInfo: DisplayedInfo,
    val material: Material = Material.PAPER,
    val customModelData: Int = 0
) {
    val id: Long? = null
    val gui: GUI? = null

    fun item() = ItemStack(material).apply {
        editMeta {
            it.displayName(displayedInfo.displayName())
            it.lore(displayedInfo.description())
            it.setCustomModelData(customModelData)
        }
    }

    fun item(replace: (String) -> String) = ItemStack(material).apply {
        editMeta {
            it.displayName(displayedInfo.displayName(replace))
            it.lore(displayedInfo.description(replace))
            it.setCustomModelData(customModelData)
        }
    }
}