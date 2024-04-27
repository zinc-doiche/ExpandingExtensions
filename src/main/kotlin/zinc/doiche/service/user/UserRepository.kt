package zinc.doiche.service.user

import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.structure.Repository
import zinc.doiche.service.user.`object`.QUser
import zinc.doiche.service.user.`object`.QUser.user
import zinc.doiche.service.user.`object`.User

object UserRepository: Repository<User> {
    override fun save(entity: User) {
        plugin.entityManager.

        TODO("Not yet implemented")
    }

    override fun findById(id: Long): User? {
        TODO("Not yet implemented")
    }

    override fun delete(entity: User) {
        TODO("Not yet implemented")
    }

    override fun update(entity: User) {
        TODO("Not yet implemented")
    }

}