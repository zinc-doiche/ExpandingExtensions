package zinc.doiche.service.item.`object`

import jakarta.persistence.*
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.minecraft.nbt.TagParser
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import zinc.doiche.database.StringArrayConverter
import zinc.doiche.lib.embeddable.DisplayedInfo
import zinc.doiche.util.serialize
import zinc.doiche.util.setTag

@Entity
@Table(name = "TBL_ITEM_DATA")
class ItemData(
    @Embedded
    val displayedInfo: DisplayedInfo,

//    @Column(unique = true, nullable = false)
//    val name: String,

    @Column(nullable = false)
    val material: Material,

//    @Column(nullable = false)
//    val displayName: String,

    @Convert(converter = StringArrayConverter::class, attributeName = "lore")
    @Column(nullable = false)
    var lore: Array<String> = emptyArray<String>(),

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    val tags: MutableMap<String, Any> = mutableMapOf()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    fun displayName() = displayedInfo.displayName()// = miniMessage().deserialize(displayName)

    fun lore() = lore.map(miniMessage()::deserialize)

    fun getItem(amount: Int = 1): ItemStack {
        val item = ItemStack(material, amount).setTag(TagParser.parseTag(tags.serialize()))
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
        return "ItemData(displayedInfo=$displayedInfo, material=$material, lore=${lore.contentToString()}, tags=$tags, id=$id)"
    }
}