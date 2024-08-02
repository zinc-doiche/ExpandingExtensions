package zinc.doiche.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.slf4j.Logger
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.Translatable
import zinc.doiche.lib.TranslationRegistry
import zinc.doiche.service.item.entity.reward.ItemReward

@TranslationRegistry
object LoggerUtil {

    @Translatable("log.prefix", defaultValue = "<brace> <aqua>ExpExt</aqua> </brace>: ")
    private lateinit var prefix: Component

    lateinit var logger: Logger
        private set

    val isInit: Boolean
        get() = this::logger.isInitialized

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