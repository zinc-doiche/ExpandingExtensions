package zinc.doiche.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.hibernate.Hibernate
import org.jboss.logging.LoggerProvider
import org.slf4j.Logger
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.TranslateRegistry

@TranslateRegistry
object LoggerUtil {
//    @Translatable("log.prefix", defaultValue = [""])
    @JvmStatic
    private val prefix = text("[ ").append(text("ExpExt", NamedTextColor.AQUA)).append(" ]: ")

    lateinit var logger: Logger
        private set

    fun init(logger: Logger) {
        if(this::logger.isInitialized) {
            throw IllegalStateException("LoggerUtil is already initialized.")
        }
        LoggerUtil.logger = logger

    }

    fun prefixed(component: Component) = prefix.append(component)

    fun prefixed(string: String) = prefix.append(string)

    fun prefixed(string: String, color: TextColor) = prefix.append(string, color)

    fun prefixedInfo(msg: String) = plugin.componentLogger.info(prefixed(msg))

    fun prefixedInfo(msg: Component) = plugin.componentLogger.info(prefixed(msg))
}