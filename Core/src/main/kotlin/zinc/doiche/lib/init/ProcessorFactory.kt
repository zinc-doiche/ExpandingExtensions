package zinc.doiche.lib.init

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.event.Listener
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.CommandFactory
import zinc.doiche.lib.CommandRegistry
import zinc.doiche.lib.ListenerRegistry
import zinc.doiche.lib.Priority
import zinc.doiche.util.LoggerUtil
import zinc.doiche.service.Service
import zinc.doiche.util.append

interface ProcessorFactory<T> {
    fun preProcess(preProcess: () -> T?): ProcessorFactory<T>
    fun process(processor: (Class<*>, T?) -> Unit): ProcessorFactory<T>
    fun postProcess(postProcess: (T?) -> Unit): ProcessorFactory<T>
    fun create(): Processor<T>

    companion object {
        fun <T> factory(): ProcessorFactory<T> {
            return ProcessorFactoryImpl()
        }

        fun listener(): Processor<Nothing> = factory<Nothing>()
            .process { clazz, _ ->
                if(clazz.isAnnotationPresent(ListenerRegistry::class.java) && Listener::class.java.isAssignableFrom(clazz)) {
                    val listener = clazz.getDeclaredConstructor().newInstance() as Listener
                    val listenerRegistry = clazz.getAnnotation(ListenerRegistry::class.java)
                    if(listenerRegistry.async) {
                        plugin.registerSuspending(listener)
                    } else {
                        plugin.register(listener)
                    }
                }
            }
            .create()

        fun command(): Processor<Nothing> = factory<Nothing>()
            .process { clazz, _ ->
                if(!clazz.isAnnotationPresent(CommandRegistry::class.java)) {
                    return@process
                }
                val commandRegistry = clazz.getDeclaredConstructor().newInstance()
                val commandMap = Bukkit.getCommandMap()
                for (method in clazz.declaredMethods) {
                    if(method.isAnnotationPresent(CommandFactory::class.java)) {
                        val factory = method.getAnnotation(CommandFactory::class.java)
                        val aliases = factory.aliases
                        val command = method.invoke(commandRegistry) as Command

                        command.aliases = aliases.toMutableList()
                        logCommand(command)
                        commandMap.register(plugin.name, command)
                    }
                }
            }
            .create()

        private fun logCommand(command: Command) = LoggerUtil.prefixedInfo(text("[")
            .append("Command", NamedTextColor.LIGHT_PURPLE)
            .append("] Registering ")
            .append("'${command.name}' ~")
            .append(command.aliases.toString()))

        fun service(): Processor<Multimap<Int, Service>> = factory<Multimap<Int, Service>>()
            .preProcess {
                Multimaps.newListMultimap(mutableMapOf(), ::mutableListOf)
            }
            .process { clazz, preObject ->
                if(clazz != Service::class.java && Service::class.java.isAssignableFrom(clazz)) {
                    val service = clazz.getDeclaredConstructor().newInstance() as Service
                    val priority = if(clazz.isAnnotationPresent(Priority::class.java))
                        clazz.getAnnotation(Priority::class.java).value
                    else
                        0
                    preObject?.let { multiMap ->
                        multiMap[priority].add(service)
                    }
                }
            }
            .postProcess { preObject ->
                preObject?.let { multiMap ->
                    multiMap.asMap().toSortedMap().forEach { (_, list) ->
                        list.forEach { plugin.register(it) }
                    }
                }
            }
            .create()
    }
}