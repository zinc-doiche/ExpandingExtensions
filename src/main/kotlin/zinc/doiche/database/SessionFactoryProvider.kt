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
            // Get all classes in the 'zinc.doiche' package that are annotated with @Entity
            val reflections = Reflections("zinc.doiche")
            val entityClasses = reflections.getTypesAnnotatedWith(Entity::class.java)

            sessionFactory = Configuration().run {
                // Add all entity classes to the configuration
                for (entityClass in entityClasses) {
                    addAnnotatedClass(entityClass)
                }

                val serviceRegistry: ServiceRegistry = StandardServiceRegistryBuilder()
                    .applySettings(this.properties)
                    .build()

                buildSessionFactory(serviceRegistry)
            }
        } catch (ex: Throwable) {
            throw ExceptionInInitializerError(ex)
        }
    }

    fun get(): SessionFactory? {
        return sessionFactory
    }
}