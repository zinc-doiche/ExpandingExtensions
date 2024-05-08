package zinc.doiche.lib.init

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.event.Listener
import org.reflections.Reflections
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.*
import zinc.doiche.service.Service
import zinc.doiche.util.*
import zinc.doiche.util.toMapOf
import java.io.File
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Comparator

interface ProcessorFactory<T> {
    fun preProcess(preProcess: () -> T?): ProcessorFactory<T>
    fun process(processor: (Reflections, T?) -> Unit): ProcessorFactory<T>
    fun postProcess(postProcess: (T?) -> Unit): ProcessorFactory<T>
    fun create(): Processor<T>

    companion object {
        fun <T> factory(): ProcessorFactory<T> {
            return ProcessorFactoryImpl()
        }

        fun listener(): Processor<Nothing> = factory<Nothing>()
            .process { reflections, _ ->
                reflections.getTypesAnnotatedWith(ListenerRegistry::class.java).forEach { clazz ->
                    if(Listener::class.java.isAssignableFrom(clazz)) {
                        val listener = clazz.getDeclaredConstructor().newInstance() as Listener
                        val listenerRegistry = clazz.getAnnotation(ListenerRegistry::class.java)
                        if(listenerRegistry.async) {
                            plugin.registerSuspending(listener)
                        } else {
                            plugin.register(listener)
                        }
                    }
                }
            }
            .create()

        fun command(): Processor<Nothing> = factory<Nothing>()
            .process { reflections, _ ->
                reflections.getTypesAnnotatedWith(CommandRegistry::class.java).forEach { clazz ->
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
            }
            .create()

        fun service(): Processor<Multimap<Int, Service>> = factory<Multimap<Int, Service>>()
            .preProcess {
                Multimaps.newListMultimap(mutableMapOf(), ::mutableListOf)
            }
            .process { reflections, preObject ->
                reflections.getSubTypesOf(Service::class.java).forEach { clazz ->
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

        fun configuration() = factory<Multimap<Int, Method>>()
            .preProcess {
                Multimaps.newListMultimap(mutableMapOf(), ::mutableListOf)
            }
            .process { reflections, multimap ->
                reflections.getTypesAnnotatedWith(Configuration()).forEach { clazz ->
                    clazz.declaredMethods.forEach { method ->
                        if(method.isAnnotationPresent(Read::class.java) && Modifier.isStatic(method.modifiers)) {
                            val priority = if(clazz.isAnnotationPresent(Priority::class.java))
                                clazz.getAnnotation(Priority::class.java).value
                            else
                                0
                            multimap!![priority].add(method)
                        }
                    }
                }
            }
            .postProcess { multimap ->
                val sorted = multimap?.asMap()
                    ?.toSortedMap(Comparator.naturalOrder<Int>().reversed()) ?: return@postProcess

                sorted.forEach { (_, list) ->
                    list.forEach { method ->
                        val type = method.parameterTypes[0]
                        val read = method.getAnnotation(Read::class.java)
                        val path = read.path
                        val originFile = File(plugin.dataFolder, path)

                        if(type == File::class.java) {
                            if(!originFile.exists()) {
                                plugin.getResource(path)?.let {
                                    plugin.saveResource(path, false)
                                } ?: run {
                                    originFile.mkdirs()
                                    originFile.createNewFile()
                                }
                            }
                            method.invoke(null, originFile)
                        } else {
                            if(!originFile.exists()) {
                                originFile.mkdirs()
                            }
                            val files = originFile.listFiles()
                            method.invoke(null, files)
                        }
                    }
                }
            }
            .create()

        fun translatable() = factory<Array<File>?>()
            .preProcess {
                File(plugin.dataFolder, "translation").let { folder ->
                    if(!folder.exists()) {
                        folder.mkdirs()
                    }
                    folder.listFiles()
                }
            }
            .process { reflections, files ->
                val miniMessage = MiniMessage.miniMessage()
                reflections.getTypesAnnotatedWith(TranslationRegistry::class.java).forEach { clazz ->
                    // find ?: create
                    val file = files?.find {
                        it.name.contains(clazz.simpleName)
                    } ?: run {
                        val path = if(clazz.packageName.contains("service")) {
                            clazz.packageName.substringAfterLast("service.")
                                .replace('.', '\\')
                                .replace("\\class", ".json")
                        } else clazz.simpleName.replace("class", "json")

                        File(plugin.dataFolder, path).apply {
                            createNewFile()
                        }
                    }

                    val map = file.toMapOf(String::class.java, Any::class.java).toMutableMap()
                    val newKeys = mutableSetOf<String>()
                    val keys = mutableSetOf<String>()

                    // read
                    clazz.declaredFields.forEach { field ->
                        if (field.isAnnotationPresent(Translatable::class.java)) {
                            val type = field.type
                            val translatable = field.getAnnotation(Translatable::class.java)
                            val key = translatable.key

                            @Suppress("UNCHECKED_CAST")
                            val value = (map[key]?.apply {
                                keys.add(key)
                            } ?: map.put(key, translatable.defaultValue).apply {
                                newKeys.add(key)
                            }) as Array<String>

                            when(type) {
                                String::class.java -> {
                                    field.set(null, value[0])
                                }
                                String::class.java.arrayType() -> {
                                    field.set(null, value)
                                }
                                Component::class.java -> {
                                    val component = miniMessage.deserialize(value[0])
                                    field.set(null, component)
                                }
                                Component::class.java.arrayType() -> {
                                    val components = value.map { miniMessage.deserialize(it) }
                                    field.set(null, components.toTypedArray())
                                }
                            }
                        }
                    }

                    //write & update
                    val keyUnion = keys.union(newKeys)
                    if(map.all { keys.contains(it.key) } && newKeys.isEmpty()) {
                        return@forEach
                    }
                    file.writeJson(map.filter { keyUnion.contains(it.key) })
                }
            }
            .create()

        private fun logCommand(command: Command) = LoggerUtil.prefixedInfo(text("[")
            .append("Command", NamedTextColor.LIGHT_PURPLE)
            .append("] Registering ")
            .append("'${command.name}' ~")
            .append(command.aliases.toString()))
    }
}