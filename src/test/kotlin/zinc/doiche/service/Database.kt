package zinc.doiche.service

import zinc.doiche.database.CachePoolFactory
import zinc.doiche.database.DatabaseFactoryProvider

internal fun init() {
    DatabaseFactoryProvider.initEntityManagerFactory(
        connectionConfig, hikariConfiguration, hibernateConfig
    )
    CachePoolFactory.initConfig(cacheConfig)
}
