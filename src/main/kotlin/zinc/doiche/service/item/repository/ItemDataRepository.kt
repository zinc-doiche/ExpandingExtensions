package zinc.doiche.service.item.repository

import jakarta.persistence.EntityManager
import zinc.doiche.Main
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.service.CachedKey
import zinc.doiche.service.Repository
import zinc.doiche.service.item.`object`.ItemData
import zinc.doiche.service.item.`object`.QItemData.itemData

class ItemDataRepository(
    override val prefix: String
) : Repository<ItemData>, CachedKey<String>() {
    private val entityManager: EntityManager by lazy {
        try {
            Main.plugin.entityManager
        } catch (e: Exception) {
            DatabaseFactoryProvider.get()?.createEntityManager() ?: throw IllegalStateException("entity manager is null")
        }
    }

    override fun save(entity: ItemData) {
        entityManager.persist(entity)
    }

    fun findByName(name: String): ItemData? = Main.plugin.query
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

    fun findByNameInCache(name: String): ItemData? {
        val id = getId(name) ?: run {
            return findByName(name)?.apply {
                saveId(name, this.id!!)
            }
        }
        return findById(id)
    }
}