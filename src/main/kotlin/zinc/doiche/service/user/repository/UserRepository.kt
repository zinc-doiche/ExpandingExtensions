package zinc.doiche.service.user.repository

import jakarta.persistence.EntityManager
import org.bukkit.entity.Player
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.service.CachedKey
import zinc.doiche.service.Repository
import zinc.doiche.service.user.`object`.QUser.user
import zinc.doiche.service.user.`object`.User
import java.util.*

class UserRepository(
    override val prefix: String
): Repository<User>, CachedKey<UUID>() {
    private val entityManager: EntityManager by lazy {
        try {
            plugin.entityManager
        } catch (e: Exception) {
            DatabaseFactoryProvider.get()?.createEntityManager() ?: throw IllegalStateException("entity manager is null")
        }
    }

    override fun save(entity: User) {
        entityManager.persist(entity)
    }

    override fun findById(id: Long): User? = entityManager.find(User::class.java, id)

    fun findByUUID(uuid: UUID): User? = plugin.query
        .select(user)
        .from(user)
        .where(user.uuid.eq(uuid))
        .fetchOne()

    override fun delete(entity: User) {
        entityManager.remove(entity)
    }

    fun findByPlayer(player: Player): User? {
        val uuid = player.uniqueId
        val id = getId(uuid) ?: return findByUUID(uuid)
        return findById(id)
    }
}