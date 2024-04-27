package zinc.doiche.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.database.`object`.Config
import zinc.doiche.util.toObject
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

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
            "javax.persistence.nonJtaDataSource" to dataSource,
//            "javax.persistence.jdbc.url" to config.getURL(),
//            "javax.persistence.jdbc.user" to config.username,
//            "javax.persistence.jdbc.password" to config.password,
            "hibernate.show_sql" to config.showSQL,
            "hibernate.hbm2ddl.auto" to config.ddl
        )
        val manager = Persistence.createEntityManagerFactory("database", properties)
        entityManagerFactory = manager
        return manager
    }
}