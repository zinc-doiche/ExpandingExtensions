package zinc.doiche.service

import zinc.doiche.database.CachePoolProvider
import zinc.doiche.database.DatabaseFactoryProvider

val hikariConfiguration = DatabaseFactoryProvider.HikariConfiguration(
    10,
    5,
    300000,
    60000
)
val hibernateConfig = DatabaseFactoryProvider.HibernateConfig(
    true,
    "create-drop",
    true
)
val connectionConfig = DatabaseFactoryProvider.ConnectionConfig(
    "localhost",
    5432,
    "postgres",
    "postgres",
    "postgres"
)
val cacheConfig = CachePoolProvider.CacheConfig(
    "localhost",
    6379,
    4,
    4,
    0,
    true,
    1,
    true,
    1
)