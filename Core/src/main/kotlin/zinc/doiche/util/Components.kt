package zinc.doiche.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.WHITE
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

fun plain(text: String): Component = text(text).color(WHITE).noItalic()

fun Component.noItalic() = this.decoration(TextDecoration.ITALIC, false)

fun Component.bold() = this.decoration(TextDecoration.BOLD, true)

fun Component.append(text: String): Component = this.append(text(text))

fun Component.append(text: String, color: TextColor): Component = this.append(text(text, color))

fun Component.replace(match: String, replacement: String): Component = this.replaceText { builder ->
    builder.matchLiteral(match).replacement(replacement)
}

fun Component.replace(match: String, replacement: Component): Component = this.replaceText { builder ->
    builder.matchLiteral(match).replacement(replacement)
}

