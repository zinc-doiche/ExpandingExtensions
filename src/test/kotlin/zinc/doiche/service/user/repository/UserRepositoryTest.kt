package zinc.doiche.service.user.repository

import org.junit.jupiter.api.Test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import zinc.doiche.service.Repository
import zinc.doiche.service.RepositoryTest
import zinc.doiche.service.user.UserService
import zinc.doiche.service.user.`object`.User
import java.util.*

class UserRepositoryTest: RepositoryTest<User>() {
    override val repository: Repository<User> = UserService.repository
    override val logger: Logger = LoggerFactory.getLogger(UserRepositoryTest::class.java)

    fun saveOne(): User {
        val user = User(UUID.randomUUID())
        repository.transaction {
            repository.save(user)
        }
        return user
    }

    @Test
    fun save() {
        val user = saveOne()
        val findById = repository.findById(user.id!!)
        logger.info("user: $user")
        logger.info("findById: $findById")
        assert(user == findById)
    }

    @Test
    fun findById() {
        val user = saveOne()
        val findById = repository.findById(user.id!!)
        logger.info("user: $user")
        logger.info("findById: $findById")
        assert(user == findById)
    }

    @Test
    fun findByUUID() {
        val user = saveOne()
        val findByUUID = UserService.repository.findByUUID(user.uuid)
        logger.info("user: $user")
        logger.info("findByUUID: $findByUUID")
        assert(user == findByUUID)
    }

    @Test
    fun delete() {
        val user = saveOne()
        repository.transaction {
            repository.delete(user)
        }
        val findById = repository.findById(user.id!!)
        logger.info("user: $user")
        logger.info("findById: $findById")
    }
}