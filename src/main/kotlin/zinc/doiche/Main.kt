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

        private fun initPluginInst(plugin: Main) {
            this.plugin = plugin
        }
    }

    val entityManager: EntityManager by lazy { DatabaseFactoryProvider.get().createEntityManager() }

    val query: JPAQueryFactory by lazy { JPAQueryFactory(entityManager) }

    val services: List<Service> = mutableListOf()

    override fun onLoad() {
        initPluginInst(this)
        initEntityManager()
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

    private fun processAll() {
        ClassLoader()
//            .add(ProcessorFactory.configuration())
//            .add(ProcessorFactory.translatable())
            .add(ProcessorFactory.service())
            .add(ProcessorFactory.command())
            .add(ProcessorFactory.listener())
            .process()
    }

    private fun loadServices() {
        for (service in services) {
            LoggerUtil.prefixedInfo(text("[ ").append("Service", NamedTextColor.DARK_AQUA).append(" ] ")
                .append("Loading").append(service::class.simpleName!!, NamedTextColor.YELLOW))
            service.onLoad()
        }
    }

    private fun initEntityManager() {
        if(entityManager.isOpen) {
            LoggerUtil.prefixedInfo("DB 연결 완료.")
        }
    }
}