package zinc.doiche.util

import net.minecraft.nbt.CompoundTag
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

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
