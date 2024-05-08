package zinc.doiche.util

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.nbt.CompoundTag
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import zinc.doiche.lib.embeddable.DisplayedInfo
import zinc.doiche.service.item.ItemDataService
import zinc.doiche.service.item.entity.ItemData

internal fun ItemStack.editTag(block: (CompoundTag) -> Unit): ItemStack {
    val item = CraftItemStack.unwrap(this)
    block(item.orCreateTag)
    return CraftItemStack.asBukkitCopy(item)
}

internal val ItemStack.tag: CompoundTag?
    get() = CraftItemStack.unwrap(this).tag

internal fun ItemStack.setTag(tag: CompoundTag): ItemStack {
    val unwrap = CraftItemStack.unwrap(this)
    unwrap.tag = tag
    return CraftItemStack.asBukkitCopy(unwrap)
}

internal fun ItemStack.hasTag() = CraftItemStack.unwrap(this).hasTag()

internal fun ItemStack.toData(name: String = this.type.name) = ItemData(
    DisplayedInfo(
        name,
        this.itemMeta.displayName()?.let { MiniMessage.miniMessage().serialize(it) } ?: "",
        this.lore()?.map {
            MiniMessage.miniMessage().serialize(it)
        }?.toTypedArray()
    ),
    this.type,
    tag?.apply { remove("id") }?.asString?.asMap() ?: mutableMapOf()
)

internal fun ItemStack.toPersistData(): ItemData? = tag?.getString("id")?.toLongOrNull()?.let { id ->
    ItemDataService.repository.findById(id)
}
