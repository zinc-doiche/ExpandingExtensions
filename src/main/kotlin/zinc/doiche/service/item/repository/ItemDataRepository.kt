package zinc.doiche.service.item.repository

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisFactory
import zinc.doiche.Main
import zinc.doiche.lib.structure.Repository
import zinc.doiche.service.item.`object`.ItemData
import zinc.doiche.service.item.`object`.QItemData.itemData

class ItemDataRepository: Repository<ItemData> {
    override fun save(entity: ItemData) {
        Main.plugin.entityManager.persist(entity)
    }

    fun findByName(name: String): ItemData? {


        return Main.plugin.query.selectFrom(itemData)
            .where(itemData.name.eq(name))
            .fetchFirst()
    }

    override fun findById(id: Long): ItemData? {
        return Main.plugin.entityManager.find(ItemData::class.java, id)
    }

    override fun delete(entity: ItemData) {
        Main.plugin.entityManager.remove(entity)
    }
}