package zinc.doiche.lib.log

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.annotation.Translatable
import zinc.doiche.lib.annotation.TranslateRegistry
import zinc.doiche.util.append

@TranslateRegistry
object LoggerUtil {
//    @Translatable("log.prefix", defaultValue = [""])
    @JvmStatic
    private val prefix = text("[ ").append(text("ExpExt", NamedTextColor.AQUA)).append(" ]: ")

    fun prefixedInfo(msg: String) = plugin.componentLogger.info(prefix.append(msg))

    fun prefixedInfo(msg: Component) = plugin.componentLogger.info(prefix.append(msg))
}