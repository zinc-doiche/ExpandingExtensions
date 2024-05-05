package zinc.doiche.service

import org.slf4j.Logger
import zinc.doiche.database.CachePoolFactory
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.util.LoggerUtil

internal fun init(logger: Logger) {
    if(!LoggerUtil.isInit) {
        LoggerUtil.init(logger)
    }
    if (!DatabaseFactoryProvider.isInit) {
        DatabaseFactoryProvider.initEntityManagerFactory(
            connectionConfig, hikariConfiguration, hibernateConfig
        )
    }
    if(!CachePoolFactory.isInit) {
        CachePoolFactory.initConfig(cacheConfig)
    }
}
