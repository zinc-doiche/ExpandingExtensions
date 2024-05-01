package zinc.doiche.service

import redis.clients.jedis.JedisPooled
import zinc.doiche.Main
import zinc.doiche.database.CachePoolFactory

abstract class CachedKey<I> {
    protected abstract val prefix: String
    private val jedisPooled: JedisPooled by lazy {
        try {
            Main.plugin.jedisPooled
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