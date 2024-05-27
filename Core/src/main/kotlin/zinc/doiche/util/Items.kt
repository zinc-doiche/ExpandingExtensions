package zinc.doiche.util

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import zinc.doiche.lib.embeddable.DisplayedInfo
import zinc.doiche.service.item.ItemDataService
import zinc.doiche.service.item.entity.ItemData

internal fun ItemStack.editTag(
    builder: (DataComponentPatch.Builder) -> DataComponentPatch
): ItemStack = CraftItemStack.unwrap(this).apply {
    val components = builder(DataComponentPatch.builder())
    applyComponents(components)
}.asBukkitCopy()

internal val ItemStack.tag: CompoundTag?
    get() = CraftItemStack.unwrap(this).components.get(DataComponents.CUSTOM_DATA)?.copyTag()

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
