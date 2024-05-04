package zinc.doiche.util

import jakarta.persistence.EntityTransaction
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.database.SessionFactoryProvider

internal inline fun transaction(crossinline block: (EntityTransaction) -> Unit) =
    plugin.entityManager.transaction.run {
//    SessionFactoryProvider.get()?.inTransaction { session ->
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
