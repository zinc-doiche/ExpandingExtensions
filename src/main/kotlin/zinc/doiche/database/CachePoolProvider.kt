package zinc.doiche.database

import redis.clients.jedis.ConnectionPoolConfig
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisPooled
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.util.toObject
import java.time.Duration


object CachePoolProvider {
    private const val REDIS_CONFIG_PATH = "database/redis.json"
    private var cachePool: JedisPooled? = null

    fun get() = cachePool

    fun create() {
        val config = plugin.config(REDIS_CONFIG_PATH).toObject(CacheConfig::class.java)
        initCachePool(config)
    }

    fun initCachePool(config: CacheConfig) {
        val poolConfig = ConnectionPoolConfig().apply {
            // maximum active connections in the pool,
            // tune this according to your needs and application type
            // default is 8
            maxTotal = config.maxTotal
            // maximum idle connections in the pool, default is 8
            maxIdle = config.maxIdle
            // minimum idle connections in the pool, default 0
            minIdle = config.minIdle
            // Enables waiting for a connection to become available.
            blockWhenExhausted = config.blockWhenExhausted
            // The maximum number of seconds to wait for a connection to become available
            setMaxWait(Duration.ofSeconds(config.maxWaitSeconds))
            // Enables sending a PING command periodically while the connection is idle.
            testWhileIdle = config.testWhileIdle
            // controls the period between checks for idle connections in the pool
            timeBetweenEvictionRuns = Duration.ofSeconds(config.timeBetweenEvictionRunsSeconds)
        }
        cachePool = JedisPooled(
            config.hostAndPort,
            DefaultJedisClientConfig.builder()
                .socketTimeoutMillis(5000)  // set timeout to 5 seconds
                .connectionTimeoutMillis(5000) // set connection timeout to 5 seconds
                .build()
            , poolConfig
        )
    }

    data class CacheConfig(
        val host: String,
        val port: Int,
        val maxTotal: Int,
        val maxIdle: Int,
        val minIdle: Int,
        val blockWhenExhausted: Boolean,
        val maxWaitSeconds: Long,
        val testWhileIdle: Boolean,
        val timeBetweenEvictionRunsSeconds: Long
    ) {
        val hostAndPort: HostAndPort
            get() = HostAndPort(host, port)
    }
}