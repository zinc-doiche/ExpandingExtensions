package zinc.doiche.service

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.slf4j.Logger

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class RepositoryTest<E> {
    abstract val repository: Repository<E>
    abstract val logger: Logger
    private var hasInit: Boolean = false

    @BeforeAll
    fun setUp() {
        if(hasInit) {
            return
        }
        hasInit = true
        init(logger)
    }
}