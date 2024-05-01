package zinc.doiche.util

import jakarta.persistence.EntityTransaction
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.database.SessionFactoryProvider

internal inline fun transaction(crossinline block: (EntityTransaction) -> Unit) = SessionFactoryProvider.get()?.inTransaction { session ->
    val transaction = session.transaction
    runCatching {
        transaction.begin()
        block(transaction)
    }.onFailure {
        plugin.slF4JLogger.warn("트랜잭션 중 실패. Rollback 실행.")
        transaction.rollback()
    }.onSuccess {
        transaction.commit()
    }
}
