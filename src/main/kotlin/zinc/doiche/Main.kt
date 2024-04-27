package zinc.doiche

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.lib.init.ClassLoader
import zinc.doiche.lib.init.ProcessorFactory
import java.io.File
import javax.persistence.EntityManager

internal lateinit var plugin: Main
    private set

class Main: JavaPlugin() {
    lateinit var entityManager: EntityManager
        private set

    override fun onEnable() {
        plugin = this
        entityManager = DatabaseFactoryProvider().get().createEntityManager()
//        processAll()
    }

    override fun onLoad() {
    }

    override fun onDisable() {
    }

    fun config(name: String): File {
        val file = File(dataFolder, name)
        if (!file.exists()) {
            saveResource(name, false)
        }
        return file
    }

    fun register(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    private fun processAll() {
        ClassLoader()
            .add { factory ->
                factory.target(ProcessorFactory.Target.ANNOTATION)
                    .preProcess { map -> map["annotation"] = "zinc.doiche.annotation.Plugin" }
                    .process { clazz -> println(clazz) }
                    .postProcess { map -> println(map) }
            }.process()
    }
}