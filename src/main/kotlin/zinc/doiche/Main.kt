package zinc.doiche

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import redis.clients.jedis.JedisPooled
import zinc.doiche.database.CachePoolFactory
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.database.SessionFactoryProvider
import zinc.doiche.lib.init.ClassLoader
import zinc.doiche.lib.init.ProcessorFactory
import zinc.doiche.service.Service
import zinc.doiche.lib.log.LoggerUtil
import zinc.doiche.util.append
import java.io.File

class Main: JavaPlugin() {
    internal companion object {
        lateinit var plugin: Main
            private set
    }

    val entityManager: EntityManager by lazy {
        val factory = DatabaseFactoryProvider.get() ?: throw IllegalStateException("factory is null")
        factory.createEntityManager()
    }

    val jedisPooled: JedisPooled by lazy {
        CachePoolFactory().create() ?: throw IllegalStateException("jedis pooled is null")
    }

    val query: JPAQueryFactory by lazyOf(JPAQueryFactory(entityManager))

    private val services: MutableList<Service> = mutableListOf()

    override fun onLoad() {
        initPluginInst(this)
        DatabaseFactoryProvider.create()
        SessionFactoryProvider.create()
        initJedisPooled()
        processAll()
        loadServices()
    }

    override fun onEnable() {
        services.forEach(Service::onEnable)
    }

    override fun onDisable() {
        services.forEach(Service::onDisable)
        jedisPooled.close()
        DatabaseFactoryProvider.close()
        entityManager.close()
    }

    fun config(name: String): File = File(dataFolder, name).let {
        if (!file.exists()) {
            file.mkdirs()
            saveResource(name, true)
        }
        File(dataFolder, name)
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

    private fun initJedisPooled() {
        jedisPooled.ping()
    }

    private fun loadServices() {
        for (service in services) {
            LoggerUtil.prefixedInfo(text("[").append("Service", NamedTextColor.DARK_AQUA).append("] ")
                .append("Loading").append(service::class.simpleName!!, NamedTextColor.YELLOW))
            service.onLoad()
        }
    }
}