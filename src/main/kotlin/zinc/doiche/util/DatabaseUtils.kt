package zinc.doiche.util

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityTransaction
import zinc.doiche.Main.Companion.plugin

internal fun transaction(block: (EntityTransaction) -> Unit) = plugin.entityManager.transaction(block)

internal fun EntityManager.transaction(block: (EntityTransaction) -> Unit) = transaction.run {
    runCatching {
        begin()
        block(this)
    }.onFailure {
        plugin.slF4JLogger.warn("트랜잭션 중 실패. Rollback 실행.")
        rollback()
    }.onSuccess {
        commit()
    }
}