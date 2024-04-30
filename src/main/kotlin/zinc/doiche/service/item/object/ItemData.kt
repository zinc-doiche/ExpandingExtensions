package zinc.doiche.service.item.`object`

import io.hypersistence.utils.hibernate.type.array.StringArrayType
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.minecraft.nbt.TagParser
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.hibernate.annotations.Type
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

    @Type(StringArrayType::class)
    @Column(nullable = false)
    val lore: List<String> = emptyList(),

    @Type(JsonType::class)
    @Column(nullable = false, columnDefinition = "json")
    val tags: Map<String, Any> = emptyMap()
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
}