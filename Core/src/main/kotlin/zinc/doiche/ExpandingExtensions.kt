package zinc.doiche

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.Listener
import redis.clients.jedis.JedisPooled
import zinc.doiche.database.CachePoolFactory
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.lib.init.ClassLoader
import zinc.doiche.lib.init.ProcessorFactory
import zinc.doiche.service.Service
import zinc.doiche.util.LoggerUtil
import zinc.doiche.util.append
import zinc.doiche.socket.SocketLinked
import zinc.doiche.socket.SocketManagerFactory
import zinc.doiche.socket.SocketManger
import java.io.File

abstract class ExpandingExtensions: SuspendingJavaPlugin(), SocketLinked {
    companion object {
        lateinit var plugin: ExpandingExtensions
            private set
    }

    val entityManager: EntityManager by lazy {
        val factory = DatabaseFactoryProvider.create() ?: throw IllegalStateException("factory is null")
        factory.createEntityManager()
    }

    val jedisPooled: JedisPooled by lazy {
        CachePoolFactory().create() ?: throw IllegalStateException("jedis pooled is null")
    }

    val query: JPAQueryFactory by lazy {
        JPAQueryFactory(entityManager)
    }

    override val socketManager: SocketManger by lazy {
        val factory = SocketManagerFactory()
        factory.create()
    }

    private val services: MutableList<Service> = mutableListOf()

    override suspend fun onLoadAsync() {
        initPluginInst(this)
        LoggerUtil.init(slF4JLogger)
//        initEntityManager()
//        initJedisPooled()
        processAll()
        loadServices()
    }

    override suspend fun onEnableAsync() {
        openSocket()

        services.forEach(Service::onEnable)
        processListeners()
    }

    override suspend fun onDisableAsync() {
        unloadServices()
        socketManager.close()
        jedisPooled.close()
        entityManager.close()
        DatabaseFactoryProvider.close()
    }

    fun config(name: String): File = File(dataFolder, name).apply {
        if (!exists()) {
            saveResource(name, false)
        }
    }

    fun register(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    fun registerSuspending(listener: Listener) {
        server.pluginManager.registerSuspendingEvents(listener, this)
    }

    fun register(service: Service) {
        services.add(service)
    }

    private fun processAll() {
        ClassLoader()
            .add(ProcessorFactory.configuration())
            .add(ProcessorFactory.translatable {
                it.replace("<brace>", "[")
                    .replace("</brace>", "]")
                    .replace("<curlyBrace>", "{")
                    .replace("</curlyBrace>", "}")
            })
            .add(ProcessorFactory.service())
            .add(ProcessorFactory.command())
            .process()
    }

    private fun processListeners() {
        ClassLoader()
            .add(ProcessorFactory.listener())
            .process()
    }

    private fun initPluginInst(plugin: ExpandingExtensions) {
        ExpandingExtensions.plugin = plugin
    }

    private fun initEntityManager() {
        if(entityManager.isOpen) {
            slF4JLogger.info("EntityManager Open")
        }
    }

    private fun initJedisPooled() {
        jedisPooled.ping()
    }

    private suspend fun openSocket() {
        socketManager.connect()
    }

    private fun loadServices() {
        for (service in services) {
            LoggerUtil.prefixedInfo(text("[").append("Service", NamedTextColor.DARK_AQUA).append("] ")
                .append("Loading ").append(service::class.simpleName!!, NamedTextColor.YELLOW))
            service.onLoad()
        }
    }

    private fun unloadServices() {
        for (service in services) {
            LoggerUtil.prefixedInfo(text("[").append("Service", NamedTextColor.DARK_AQUA).append("] ")
                .append("Unloading ").append(service::class.simpleName!!, NamedTextColor.YELLOW))
            service.onDisable()
        }
    }
}