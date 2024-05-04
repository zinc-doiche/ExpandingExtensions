package zinc.doiche.service.item.repository

import zinc.doiche.service.CachedKeyRepository
import zinc.doiche.service.item.`object`.ItemData
import zinc.doiche.service.item.`object`.QItemData.itemData

class ItemDataRepository(
    override val prefix: String
) : CachedKeyRepository<String, ItemData>() {
    override fun save(entity: ItemData) {
        entityManager.persist(entity)
    }

    fun findByName(name: String): ItemData? = query
        .selectFrom(itemData)
        .where(itemData.name.eq(name))
        .fetchFirst()

    override fun findById(id: Long): ItemData? {
        return entityManager.find(ItemData::class.java, id)
    }

    override fun delete(entity: ItemData) {
        removeId(entity.name)
        entityManager.remove(entity)
    }

    fun findCachedByName(name: String): ItemData? {
        val id = getId(name) ?: run {
            return findByName(name)?.apply {
                saveId(name, this.id!!)
            }
        }
        return findById(id)
    }
}