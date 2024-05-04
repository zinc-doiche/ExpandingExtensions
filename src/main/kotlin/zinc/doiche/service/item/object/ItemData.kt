package zinc.doiche.service.item.`object`

import io.hypersistence.utils.hibernate.type.array.StringArrayType
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.minecraft.nbt.TagParser
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.hibernate.annotations.JdbcType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.Type
import org.hibernate.type.SqlTypes
import zinc.doiche.util.serialize
import zinc.doiche.util.setTag

@Entity
@Table(name = "ITEM_DATA")
class ItemData(
    @Column(unique = true, nullable = false)
    val name: String,

    @Column(nullable = false)
    val material: Material,

    @Column(nullable = false)
    val displayName: String,

//    @Type(StringArrayType::class)
    @Convert(converter = AttributeConverter::class)
    @Column(nullable = false)
    var lore: Array<String> = emptyArray<String>(),

//    @Type(JsonType::class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    val tags: MutableMap<String, Any> = mutableMapOf()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    fun displayName() = miniMessage().deserialize(displayName)

    fun lore() = lore.map(miniMessage()::deserialize)

    fun getItem(amount: Int = 1) = ItemStack(material, amount).apply {
        lore(this@ItemData.lore())
        editMeta { meta -> meta.displayName(this@ItemData.displayName()) }
        setTag(TagParser.parseTag(tags.serialize()))
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
}