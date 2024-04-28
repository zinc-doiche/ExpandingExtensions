package zinc.doiche

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.hibernate.cache.spi.RegionFactory
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.lib.init.ClassLoader
import zinc.doiche.lib.init.ProcessorFactory
import zinc.doiche.lib.structure.Service
import zinc.doiche.lib.log.LoggerUtil
import zinc.doiche.service.user.UserService
import zinc.doiche.util.append
import java.io.File

class Main: JavaPlugin() {
    internal companion object {
        lateinit var plugin: Main
            private set
    }

    val entityManager: EntityManager by lazy {
        DatabaseFactoryProvider.get().createEntityManager()
    }

    val query: JPAQueryFactory by lazy {
        JPAQueryFactory(entityManager)
    }

    val services: List<Service> = mutableListOf()

    override fun onLoad() {
        plugin = this
        if(entityManager.isOpen) {
            LoggerUtil.prefixedInfo("DB 연결 완료.")
        }
//        processAll()

        (services as MutableList<Service>).add(UserService())

        for (service in services) {
            LoggerUtil.prefixedInfo(text("[ Service ] -").append(" Loading ")
                .append(service::class.simpleName!!, NamedTextColor.YELLOW))
            service.onLoad()
        }
    }

    override fun onEnable() {
        for (service in services) {
            service.onEnable()
        }
    }

    override fun onDisable() {
        for (service in services) {
            service.onDisable()
        }
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
            .add(ProcessorFactory.listener())
//            .add(ProcessorFactory.translatable())
//            .add(ProcessorFactory.command())
            .add(ProcessorFactory.service())
            .process()
    }
}