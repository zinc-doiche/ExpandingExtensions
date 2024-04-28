package zinc.doiche.service.user.repository

import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.structure.Repository
import zinc.doiche.service.user.`object`.QUser.user
import zinc.doiche.service.user.`object`.User
import java.util.*

class UserRepository: Repository<User> {

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
}