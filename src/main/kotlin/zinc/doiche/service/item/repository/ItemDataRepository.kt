package zinc.doiche.service.item.repository

import com.querydsl.core.types.Projections
import zinc.doiche.database.`object`.Pageable
import zinc.doiche.database.repository.CachedKeyRepository
import zinc.doiche.service.item.`object`.ItemData
import zinc.doiche.service.item.`object`.QItemData.itemData

class ItemDataRepository(
    override val prefix: String
) : CachedKeyRepository<String, ItemData>() {
    override fun save(entity: ItemData) {
        entityManager.persist(entity)
    }

    fun findAllNames(): List<String> = query
        .select(itemData.name)
        .from(itemData)
        .fetch()

    fun findByPage(page: Int, size: Long): Pageable<String> {
        val count = query.select(itemData.count())
            .from(itemData)
            .fetchOne() ?: 0
        val content = query.select(itemData.name)
            .from(itemData)
            .orderBy(itemData.name.asc())
            .offset((page - 1) * size)
            .limit(size)
            .fetch()
        return Pageable(content, count, page)
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