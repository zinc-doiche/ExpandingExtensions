package zinc.doiche.service.image.entity

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

class Image(
    val key: String,
    val unicode: String
) {
    val id: Long? = null

    fun asTitle(): Component = Component.text(unicode).color(TextColor.fromHexString("#FEFEFE"))
}