package zinc.doiche.service

import zinc.doiche.database.CachePoolProvider
import zinc.doiche.database.DatabaseFactoryProvider

internal fun init() {
    DatabaseFactoryProvider.initEntityManagerFactory(
        connectionConfig, hikariConfiguration, hibernateConfig
    )
    CachePoolProvider.initCachePool(cacheConfig)
}
