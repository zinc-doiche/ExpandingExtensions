package zinc.doiche.database

import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.service.ServiceRegistry
import org.reflections.Reflections
import jakarta.persistence.Entity

object SessionFactoryProvider {
    private var sessionFactory: SessionFactory? = null

    fun create() {
        try {
            val configuration = Configuration()

            // Get all classes in the 'zinc.doiche' package that are annotated with @Entity
            val reflections = Reflections("zinc.doiche")
            val entityClasses = reflections.getTypesAnnotatedWith(Entity::class.java)

            // Add all entity classes to the configuration
            for (entityClass in entityClasses) {
                configuration.addAnnotatedClass(entityClass)
            }

            val serviceRegistry: ServiceRegistry = StandardServiceRegistryBuilder()
                .applySettings(configuration.properties)
                .build()

            sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (ex: Throwable) {
            throw ExceptionInInitializerError(ex)
        }
    }

    fun get(): SessionFactory? {
        return sessionFactory
    }
}