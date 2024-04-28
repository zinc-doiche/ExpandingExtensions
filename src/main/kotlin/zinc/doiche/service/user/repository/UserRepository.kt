package zinc.doiche.service.user.repository

import org.bukkit.entity.Player
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.structure.Repository
import zinc.doiche.service.user.`object`.QUser.user
import zinc.doiche.service.user.`object`.User
import java.util.*

class UserRepository: Repository<User> {
    val userIDMap = mutableMapOf<UUID, Long>()

    override fun save(entity: User) {
        plugin.entityManager.persist(entity)
    }

    override fun findById(id: Long): User? = plugin.entityManager.find(User::class.java, id)

    fun findByUUID(uuid: UUID): User? = plugin.query
        .select(user)
        .from(user)
        .where(user.uuid.eq(uuid))
        .fetchOne()

    override fun delete(entity: User) {
        plugin.entityManager.remove(entity)
    }

    fun findByPlayer(player: Player): User? {
        val uuid = player.uniqueId
        val id = userIDMap[uuid] ?: return findByUUID(uuid)
        return findById(id)
    }
}