package zinc.doiche.service

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import zinc.doiche.Main
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.util.transaction

abstract class Repository<E> {
    abstract fun save(entity: E)
    abstract fun findById(id: Long): E?
    abstract fun delete(entity: E)

    protected val entityManager: EntityManager by lazy {
        try {
            Main.plugin.entityManager
        } catch (e: Exception) {
            DatabaseFactoryProvider.get()?.createEntityManager() ?: throw IllegalStateException("entity manager is null")
        }
    }

    fun transaction(block: (EntityManager) -> Unit) {
        entityManager.transaction {
            block(entityManager)
        }
    }

    protected val query: JPAQueryFactory by lazy {
        try {
            Main.plugin.query
        } catch (e: Exception) {
            JPAQueryFactory(entityManager)
        }
    }
}