package zinc.doiche.service

import com.github.shynixn.mccoroutine.bukkit.MCCoroutine
import com.github.shynixn.mccoroutine.bukkit.test.TestMCCoroutine
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.slf4j.Logger
import zinc.doiche.database.repository.Repository

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class RepositoryTest<E> {
    abstract val repository: Repository<E>
    abstract val logger: Logger
    private var hasInit: Boolean = false

    init {
        MCCoroutine.Driver = TestMCCoroutine.Driver
    }

    @BeforeAll
    fun setUp() {
        if(hasInit) {
            return
        }
        hasInit = true
        init(logger)
    }
}