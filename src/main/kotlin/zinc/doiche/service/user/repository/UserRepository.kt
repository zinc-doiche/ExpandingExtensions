package zinc.doiche.service.user.repository

import org.bukkit.entity.Player
import zinc.doiche.service.CachedKeyRepository
import zinc.doiche.service.user.`object`.QUser.user
import zinc.doiche.service.user.`object`.User
import java.util.*

class UserRepository(
    override val prefix: String
): CachedKeyRepository<UUID, User>() {

    override fun save(entity: User) {
        entityManager.persist(entity)
    }

    override fun findById(id: Long): User? = entityManager.find(User::class.java, id)

    fun findByUUID(uuid: UUID): User? = query
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