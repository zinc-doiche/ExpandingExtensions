package zinc.doiche.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.util.toObject

object DatabaseFactoryProvider {
    private const val CONNECTION_CONFIG_PATH = "database/connection.json"
    private const val HIKARI_CONFIG_PATH = "database/hikari.json"
    private const val HIBERNATE_CONFIG_PATH = "database/hibernate.json"

    private var entityManagerFactory: EntityManagerFactory? = null

    fun get() = entityManagerFactory

    fun close() {
        entityManagerFactory?.close()
    }

    fun create() {
        if(entityManagerFactory != null) {
            return
        }
        val connectionConfig = plugin.config(CONNECTION_CONFIG_PATH).toObject(ConnectionConfig::class.java)
        val hikariConfig = plugin.config(HIKARI_CONFIG_PATH).toObject(HikariConfiguration::class.java)
        val hibernateConfig = plugin.config(HIBERNATE_CONFIG_PATH).toObject(HibernateConfig::class.java)
        initEntityManagerFactory(connectionConfig, hikariConfig, hibernateConfig)
    }

    fun initEntityManagerFactory(
        connectionConfig: ConnectionConfig,
        hikariConfiguration: HikariConfiguration,
        hibernateConfig: HibernateConfig
    ): EntityManagerFactory {
        Thread.currentThread().contextClassLoader = javaClass.classLoader

        val hikariConfig = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = connectionConfig.getURL()
            username = connectionConfig.username
            password = connectionConfig.password
            isAutoCommit = hibernateConfig.isAutoCommit
            maximumPoolSize = hikariConfiguration.maximumPoolSize
            minimumIdle = hikariConfiguration.minimumIdle
            idleTimeout = hikariConfiguration.idleTimeout
            connectionTimeout = hikariConfiguration.connectionTimeout

            initializationFailTimeout = -1
        }
        val dataSource = HikariDataSource(hikariConfig)
        val properties = mapOf(
            "jakarta.persistence.nonJtaDataSource" to dataSource,
            "hibernate.show_sql" to hibernateConfig.showSQL,
//            "hibernate.dialect" to "org.hibernate.dialect.PostgreSQLDialect",
            "hibernate.hbm2ddl.auto" to hibernateConfig.hbm2ddl,
            "hibernate.cache.use_second_level_cache" to true,
            "hibernate.globally_quoted_identifiers" to true,
            "hibernate.cache.region.factory_class" to "org.hibernate.cache.jcache.internal.JCacheRegionFactory"
        )

        this.entityManagerFactory = Persistence.createEntityManagerFactory("database", properties)
        return entityManagerFactory!!
    }

    data class ConnectionConfig(
        val host: String,
        val port: Int,
        val database: String,
        val username: String,
        val password: String,
    ) {
        fun getURL(): String = "jdbc:postgresql://$host:$port/$database"
    }

    data class HikariConfiguration(
        val maximumPoolSize: Int,
        val minimumIdle: Int,
        val idleTimeout: Long,
        val connectionTimeout: Long
    )

    data class HibernateConfig(
        val showSQL: Boolean,
        val hbm2ddl: String,
        val isAutoCommit: Boolean
    )
}