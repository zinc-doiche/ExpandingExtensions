package zinc.doiche.service.gui.entity

import jakarta.persistence.*
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

@Entity
@Table(name = "TBL_GUI")
class GUI(
    @Column(nullable = false, unique = true)
    val name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @OneToMany(mappedBy = "gui", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val slots: MutableList<GUISlot> = mutableListOf()

    @OneToMany(mappedBy = "gui", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val titles: MutableList<GUITitle> = mutableListOf()

    fun setItem(slot: Int, inventory: Inventory) {
        inventory.setItem(slot, slots[slot].item())
    }

    fun setItem(slot: Int, inventory: Inventory, replace: (String) -> String) {
        inventory.setItem(slot, slots[slot].item(replace))
    }

    companion object {
        fun Player.openGUI(gui: GUI, inventory: Inventory) {
            gui.slots.forEachIndexed { index, slot ->
                inventory.setItem(index, slot.item())
            }
            openInventory(inventory)
        }

        fun Player.openGUI(gui: GUI, inventory: Inventory, replace: (String) -> String) {
            gui.slots.forEachIndexed { index, slot ->
                inventory.setItem(index, slot.item(replace))
            }
            openInventory(inventory)
        }
    }

}