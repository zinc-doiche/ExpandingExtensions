package zinc.doiche.util

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityTransaction

internal inline fun EntityManager.transaction(crossinline block: (EntityTransaction) -> Unit) = transaction.run {
    runCatching {
        begin()
        block(this)
    }.onFailure { e ->
        LoggerUtil.logger.error("트랜잭션 중 실패. Rollback 실행.", e)
        rollback()
    }.onSuccess {
        commit()
    }
}
