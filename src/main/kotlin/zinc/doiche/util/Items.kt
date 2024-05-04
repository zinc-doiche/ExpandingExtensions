package zinc.doiche.util

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.nbt.CompoundTag
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import zinc.doiche.service.item.`object`.ItemData

internal fun ItemStack.editTag(block: (CompoundTag) -> Unit) = apply {
    val item = this as CraftItemStack
    block(item.handle.orCreateTag)
}

internal val ItemStack.tag: CompoundTag?
    get() = (this as CraftItemStack).handle.tag

internal fun ItemStack.setTag(tag: CompoundTag) = apply {
    (this as CraftItemStack).handle.tag = tag
}

internal fun ItemStack.hasTag() = tag != null

internal fun ItemStack.toData(name: String = this.type.name) = ItemData(
    name,
    this.type,
    this.itemMeta.displayName()?.let { MiniMessage.miniMessage().serialize(it) } ?: "",
    this.lore()?.map {
        MiniMessage.miniMessage().serialize(it)
    }?.toTypedArray() ?: emptyArray(),
    tag?.asString?.asMap() ?: mutableMapOf()
)
