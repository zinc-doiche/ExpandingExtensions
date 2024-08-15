package zinc.doiche.service.gui.entity

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.InventoryView

class GUI(
    val name: String,
    val type: GUIType
) {
    val id: Long? = null
    val slots: MutableList<GUISlot> = mutableListOf()
    val titles: MutableList<GUITitle> = mutableListOf()

    val thumbTitle: Component
        get() = titles.firstOrNull()?.image?.asTitle() ?: Component.text(name)

    fun renderItems(inventory: Inventory) {
        slots.forEachIndexed { index, slot ->
            inventory.setItem(index, slot.item())
        }
    }

    fun renderItems(inventory: Inventory, replace: (String) -> String) {
        slots.forEachIndexed { index, slot ->
            inventory.setItem(index, slot.item(replace))
        }
    }

    fun setItem(slot: Int, inventory: Inventory) {
        inventory.setItem(slot, slots[slot].item())
    }

    fun setItem(slot: Int, inventory: Inventory, replace: (String) -> String) {
        inventory.setItem(slot, slots[slot].item(replace))
    }

    fun openInventoryView(player: Player, holder: InventoryHolder): InventoryView? {
        return if(type.isGeneric) {
            val inventory = Bukkit.createInventory(holder, type.size, thumbTitle)
            player.openInventory(inventory)
        } else {
            return when(type) {
                GUIType.ANVIL -> player.openAnvil(null, true)
                GUIType.MERCHANT -> {
                    val merchant = Bukkit.createMerchant(thumbTitle)
                    player.openMerchant(merchant, true)
                }
                GUIType.BEACON -> {
                    //TODO Impl
                    null
                }
                GUIType.BREWING -> {
                    //TODO Impl
                    null
                }
                GUIType.CRAFTING -> player.openWorkbench(null, true)
                GUIType.ENCHANTING -> player.openEnchanting(null, true)
                GUIType.FURNACE -> {
                    //TODO Impl
                    null
                }
                GUIType.GRINDSTONE -> player.openGrindstone(null, true)
                else -> null
            }
        }
    }
}

enum class GUIType(
    val size: Int
) {
    GENERIC_9X1(9),
    GENERIC_9X2(18),
    GENERIC_9X3(27),
    GENERIC_9X4(36),
    GENERIC_9X5(45),
    GENERIC_9X6(54),
    ANVIL(3),
    MERCHANT(3),
    BEACON(1),
    BREWING(5),
    CRAFTING(10),
    ENCHANTING(2),
    FURNACE(3),
    GRINDSTONE(1),
    ;

    val isGeneric = this.name.contains("GENERIC")
}