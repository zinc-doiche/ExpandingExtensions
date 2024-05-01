package zinc.doiche.service

import zinc.doiche.database.CachePoolFactory
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
    "1234"
)
val cacheConfig = CachePoolFactory.CacheConfig(
    "localhost",
    6379,
    "veiowb38hf",
    4,
    4,
    0,
    true,
    1,
    true,
    1
)