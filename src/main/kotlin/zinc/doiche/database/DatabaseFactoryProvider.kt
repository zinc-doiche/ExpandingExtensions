package zinc.doiche.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.util.toObject

class DatabaseFactoryProvider {
    private var entityManagerFactory: EntityManagerFactory? = null

    fun get() = entityManagerFactory ?: create()

    private fun create(): EntityManagerFactory {
        val config = plugin.config("database.json").toObject(Config::class.java)
        Thread.currentThread().contextClassLoader = javaClass.classLoader
//        Thread.currentThread().contextClassLoader = PluginClassLoader.getSystemClassLoader()
        return initEntityManagerFactory(config)
    }

    private fun initEntityManagerFactory(config: Config): EntityManagerFactory {
        val hikariConfig = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = config.getURL()
            username = config.username
            password = config.password
//            isAutoCommit = true

            maximumPoolSize = 10
            minimumIdle = 5
            idleTimeout = 60000 * 5
            connectionTimeout = 60000
        }
        val dataSource = HikariDataSource(hikariConfig)
        val properties = mapOf(
            "jakarta.persistence.nonJtaDataSource" to dataSource,
            "hibernate.show_sql" to config.showSQL,
            "hibernate.hbm2ddl.auto" to config.ddl,
            "hibernate.cache.use_second_level_cache" to true,
            "hibernate.globally_quoted_identifiers" to true,
            "hibernate.cache.region.factory_class" to "org.hibernate.cache.jcache.internal.JCacheRegionFactory",
        )

        val managerFactory = Persistence.createEntityManagerFactory("database", properties)
        entityManagerFactory = managerFactory
        return managerFactory
    }

    data class Config(
        val host: String,
        val port: Int,
        val database: String,
        val username: String,
        val password: String,
        val showSQL: String,
        val ddl: String
    ) {
        fun getURL(): String = "jdbc:postgresql://$host:$port/$database"
    }
}