package zinc.doiche

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.lib.init.ClassLoader
import zinc.doiche.lib.init.ProcessorFactory
import zinc.doiche.lib.structure.Service
import zinc.doiche.lib.log.LoggerUtil
import zinc.doiche.util.append
import java.io.File

class Main: JavaPlugin() {
    internal companion object {
        lateinit var plugin: Main
            private set
    }

    val entityManager: EntityManager by lazy {
        val factory = DatabaseFactoryProvider.get() ?: throw RuntimeException("factory is null")
        factory.createEntityManager()
    }

    val query: JPAQueryFactory by lazy { JPAQueryFactory(entityManager) }

    private val services: MutableList<Service> = mutableListOf()

    override fun onLoad() {
        initPluginInst(this)
        DatabaseFactoryProvider.create()
        processAll()
        loadServices()
    }

    override fun onEnable() {
        services.forEach(Service::onEnable)
    }

    override fun onDisable() {
        services.forEach(Service::onDisable)
        DatabaseFactoryProvider.close()
        entityManager.close()
    }

    fun config(name: String): File = File(dataFolder, name).apply {
        if (!file.exists()) {
            saveResource(name, false)
        }
    }

    fun register(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    fun register(service: Service) {
        services.add(service)
    }

    private fun processAll() {
        ClassLoader()
//            .add(ProcessorFactory.configuration())
//            .add(ProcessorFactory.translatable())
            .add(ProcessorFactory.service())
            .add(ProcessorFactory.command())
            .add(ProcessorFactory.listener())
            .process()
    }

    private fun initPluginInst(plugin: Main) {
        Main.plugin = plugin
    }

    private fun loadServices() {
        for (service in services) {
            LoggerUtil.prefixedInfo(text("[").append("Service", NamedTextColor.DARK_AQUA).append("] ")
                .append("Loading").append(service::class.simpleName!!, NamedTextColor.YELLOW))
            service.onLoad()
        }
    }
}