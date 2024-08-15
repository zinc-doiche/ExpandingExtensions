package zinc.doiche.service.item.entity

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.TagParser
import net.minecraft.world.item.component.CustomData
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import zinc.doiche.lib.embeddable.DisplayedInfo
import zinc.doiche.util.editTag
import zinc.doiche.util.serialize

class ItemData(
    val displayedInfo: DisplayedInfo,
    val material: Material,
    val tags: MutableMap<String, Any> = mutableMapOf()
) {
    val id: Long? = null

    fun displayName() = displayedInfo.displayName()// = miniMessage().deserialize(displayName)

    fun lore() = displayedInfo.description()

    fun getItem(amount: Int = 1): ItemStack {
        val tag = TagParser.parseTag(tags.apply { put("id", id!!) }.serialize())
        val item = ItemStack(material, amount).editTag { builder -> builder
            .set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
            .build()
        }
        item.editMeta { meta ->
            meta.displayName(displayName())
            meta.lore(lore())
        }
        return item
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemData

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "ItemData(displayedInfo=$displayedInfo, material=$material, tags=$tags, id=$id)"
    }
}