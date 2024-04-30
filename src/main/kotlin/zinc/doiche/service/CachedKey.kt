package zinc.doiche.service

import redis.clients.jedis.JedisPooled
import zinc.doiche.Main
import zinc.doiche.database.CachePoolProvider

abstract class CachedKey<I> {
    abstract val prefix: String
    val jedisPooled: JedisPooled by lazy {
        try {
            Main.plugin.jedisPooled
        } catch (e: Exception) {
            CachePoolProvider.get() ?: throw IllegalStateException("jedis pooled is null")
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