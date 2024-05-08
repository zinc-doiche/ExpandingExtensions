package zinc.doiche.service.gui.entity

import jakarta.persistence.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
@Table(name = "TBL_GUI_SLOT")
class GUISlot(
    @Column(nullable = false)
    val displayedInfo: DisplayedInfo,

    @Enumerated(EnumType.STRING)
    val material: Material = Material.PAPER,

    val customModelData: Int = 0
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GUI_ID")
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