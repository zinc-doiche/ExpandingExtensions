package zinc.doiche.database.repository

import redis.clients.jedis.JedisPooled
import zinc.doiche.ExpandingExtensions
import zinc.doiche.database.CachePoolFactory

abstract class CachedKeyRepository<I, E>: Repository<E>() {
    protected abstract val prefix: String

    protected val jedisPooled: JedisPooled by lazy {
        try {
            ExpandingExtensions.plugin.jedisPooled
        } catch (e: Exception) {
            CachePoolFactory().create() ?: throw IllegalStateException("jedis pooled is null")
        }
    }

    fun saveId(identifier: I, id: Long) {
        jedisPooled.set("$prefix:$identifier", id.toString())
    }

    fun removeId(identifier: I) {
        jedisPooled.del("$prefix:$identifier")
    }

    fun getId(identifier: I): Long? {
        return jedisPooled.get("$prefix:$identifier")?.toLong()
    }
}