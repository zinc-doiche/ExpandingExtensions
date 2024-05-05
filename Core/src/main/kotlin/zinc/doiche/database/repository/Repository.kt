package zinc.doiche.database.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import zinc.doiche.ExpandingExtensions
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.util.transaction

abstract class Repository<E> {
    abstract fun save(entity: E)
    abstract fun findById(id: Long): E?
    abstract fun delete(entity: E)

    protected val entityManager: EntityManager by lazy {
        try {
            ExpandingExtensions.plugin.entityManager
        } catch (e: Exception) {
            DatabaseFactoryProvider.get()?.createEntityManager() ?: throw IllegalStateException("entity manager is null")
        }
    }

    protected val query: JPAQueryFactory by lazy {
        try {
            ExpandingExtensions.plugin.query
        } catch (e: Exception) {
            JPAQueryFactory(entityManager)
        }
    }

    fun transaction(block: Repository<E>.(EntityManager) -> Unit) {
        entityManager.transaction {
            block(this, entityManager)
        }
    }
}