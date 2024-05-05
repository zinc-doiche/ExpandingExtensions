package zinc.doiche.service.item.repository

import org.bukkit.Material
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import zinc.doiche.service.RepositoryTest

import zinc.doiche.service.item.ItemDataService
import zinc.doiche.service.item.`object`.ItemData

class ItemDataRepositoryTest: RepositoryTest<ItemData>() {
    override val repository = ItemDataService.repository
    override val logger: Logger = LoggerFactory.getLogger(ItemDataRepositoryTest::class.java)

    private fun saveOne(name: String): ItemData {
        val itemData = ItemData(
            name,
            Material.PAPER,
            "<!i><rainbow>test test test",
            arrayOf(
                "<!i><white><bold>bold lore",
                "<!i><white><bold>bold lore"
            ),
            mutableMapOf(
                "key" to 1234
            )
        )
        repository.transaction {
            repository.save(itemData)
        }
        return itemData
    }

    private fun saveMany(amount: Int) {
        repository.transaction {
            repeat(amount) { i ->
                save(ItemData(
                    "test $i",
                    Material.PAPER,
                    "<!i><rainbow>test $i",
                ))
            }
        }
    }

    @Test
    fun save() {
        val itemData = saveOne("save")
        val findOne = repository.findById(itemData.id!!)

        logger.info("findOne: $findOne")
        logger.info("itemData: $itemData")
        assert(itemData == findOne)
    }

    @Test
    fun findByName() {
        val itemData = saveOne("findByName")
        repository.transaction {
            repository.save(itemData)
        }
        val findByName = repository.findByName("findByName")
        logger.info("findByName: $findByName")
        logger.info("itemData: $itemData")
        assert(findByName == itemData)
    }

    @Test
    fun findById() {
        val itemData = saveOne("findById")
        repository.transaction {
            repository.save(itemData)
        }
        val findOne = repository.findById(itemData.id!!)
        logger.info("findOne: $findOne")
        logger.info("itemData: $itemData")
        assert(itemData == findOne)
    }

    @Test
    fun findByPage() {
        saveMany(100)
        val page = repository.findByPage(1, 10)
        logger.info("page: $page")
    }

    @Test
    fun delete() {
        val itemData = saveOne("delete")
        repository.transaction {
            repository.save(itemData)
        }
        val findOne = repository.findById(itemData.id!!)
        logger.info("findOne: $findOne")
        logger.info("itemData: $itemData")
        assert(itemData == findOne)

        repository.transaction {
            repository.delete(itemData)
        }
        val findAfterDelete = repository.findById(itemData.id!!)
        logger.info("findAfterDelete: $findAfterDelete")
        assert(findAfterDelete == null)
    }
}