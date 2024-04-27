package zinc.doiche

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.lib.init.ClassLoader
import zinc.doiche.lib.init.ProcessorFactory
import java.io.File
import javax.persistence.EntityManager
import javax.xml.parsers.DocumentBuilderFactory

class Main: JavaPlugin() {
    internal companion object {
        lateinit var plugin: Main
            private set
    }

    val entityManager: EntityManager by lazy {
        DatabaseFactoryProvider().get().createEntityManager()
    }

    override fun onEnable() {
        plugin = this
        entityManager.isOpen
//        processAll()
    }

    override fun onLoad() {
    }

    override fun onDisable() {
    }

    fun config(name: String): File = File(dataFolder, name).apply {
        if (!file.exists()) {
            saveResource(name, false)
        }
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