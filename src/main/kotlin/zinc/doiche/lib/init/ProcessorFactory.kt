package zinc.doiche.lib.init

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.event.Listener
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.annotation.*
import zinc.doiche.lib.structure.Service

interface ProcessorFactory {
    fun preProcess(preProcess: (MutableMap<String, Any>) -> Unit): ProcessorFactory
    fun process(processor: (Class<*>) -> Unit): ProcessorFactory
    fun process(processor: (Class<*>, MutableMap<String, Any>) -> Unit): ProcessorFactory
    fun postProcess(postProcess: (Map<String, Any>) -> Unit): ProcessorFactory
    fun create(): Processor

    companion object {
        fun factory(): ProcessorFactory {
            return ProcessorFactoryImpl()
        }

        fun listener(): Processor = factory()
            .process { clazz ->
                if(clazz.isAnnotationPresent(ListenerRegistry::class.java)
                        && clazz.superclass.isAssignableFrom(Listener::class.java)) {
                    val listener = clazz.getDeclaredConstructor().newInstance() as Listener
                    plugin.register(listener)
                }
            }
            .create()

        fun command(): Processor = factory()
            .process { clazz ->
                if(!clazz.isAnnotationPresent(CommandRegistry::class.java)) {
                    return@process
                }
                val commandRegistry = clazz.getDeclaredConstructor().newInstance()
                val commandMap = Bukkit.getCommandMap()
                for (method in clazz.declaredMethods) {
                    if(method.isAnnotationPresent(CommandFactory::class.java)) {
                        val command = method.invoke(commandRegistry) as Command
                        commandMap.register(plugin.name, command)
                    }
                }
            }
            .create()

        @Suppress("UNCHECKED_CAST")
        fun service(): Processor = factory()
            .process { clazz, map ->
                if(clazz.superclass.isAssignableFrom(Service::class.java)) {
                    val service = clazz.getDeclaredConstructor().newInstance() as Service
                    val priority = if(clazz.isAnnotationPresent(Priority::class.java))
                        clazz.getAnnotation(Priority::class.java).value
                    else
                        0
                    val services = map["service"] as? MutableMap<Int, MutableList<Service>> ?: run {
                        map["service"] = mutableMapOf<Int, MutableList<Service>>()
                        map["service"] as MutableMap<Int, MutableList<Service>>
                    }
                    services[priority]?.add(service) ?: run {
                        services[priority] = mutableListOf(service)
                    }
                }
            }
            .postProcess { map ->
                val services = map["service"] as? MutableMap<Int, MutableList<Service>> ?: return@postProcess
                services.toSortedMap().forEach { (_, list) ->
                    list.forEach { (plugin.services as MutableList<Service>).add(it) }
                }
            }
            .create()
    }
}