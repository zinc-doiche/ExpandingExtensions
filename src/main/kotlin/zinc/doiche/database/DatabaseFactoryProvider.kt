package zinc.doiche.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.database.`object`.Config
import zinc.doiche.util.toObject

class DatabaseFactoryProvider {
    private var entityManagerFactory: EntityManagerFactory? = null

    fun get() = entityManagerFactory ?: create()

    private fun create(): EntityManagerFactory {
        val config = plugin.config("database.json").toObject(Config::class.java)
        Thread.currentThread().contextClassLoader = javaClass.classLoader
        return initEntityManagerFactory(config)
    }

    private fun initEntityManagerFactory(config: Config): EntityManagerFactory {
        val hikariConfig = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = config.getURL()
            username = config.username
            password = config.password

            maximumPoolSize = 10
            minimumIdle = 5
            idleTimeout = 60000 * 5
            connectionTimeout = 60000
            isAutoCommit = true
        }
        val dataSource = HikariDataSource(hikariConfig)
        val properties = mapOf(
            "jakarta.persistence.nonJtaDataSource" to dataSource,
//            "jakarta.persistence.jdbc.url" to config.getURL(),
//            "jakarta.persistence.jdbc.user" to config.username,
//            "jakarta.persistence.jdbc.password" to config.password,
            "hibernate.show_sql" to config.showSQL,
            "hibernate.hbm2ddl.auto" to config.ddl
        )
        val manager = Persistence.createEntityManagerFactory("database", properties)
        entityManagerFactory = manager
        return manager
    }
}