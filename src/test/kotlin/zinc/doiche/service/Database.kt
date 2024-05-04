package zinc.doiche.service

import org.slf4j.Logger
import zinc.doiche.database.CachePoolFactory
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.util.LoggerUtil

internal fun init(logger: Logger) {
    LoggerUtil.init(logger)
    DatabaseFactoryProvider.initEntityManagerFactory(
        connectionConfig, hikariConfiguration, hibernateConfig
    )
    CachePoolFactory.initConfig(cacheConfig)
}
