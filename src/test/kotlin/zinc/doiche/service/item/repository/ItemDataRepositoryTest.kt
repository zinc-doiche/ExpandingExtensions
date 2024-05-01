package zinc.doiche.service.item.repository

import org.bukkit.Material
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import zinc.doiche.service.init
import zinc.doiche.service.item.ItemDataService
import zinc.doiche.service.item.`object`.ItemData

class ItemDataRepositoryTest {
    private val repository: ItemDataRepository by lazyOf(ItemDataService.repository)

    @BeforeEach
    fun setUp() {
        init()
    }

    @Test
    fun save() {
        val itemData = ItemData(
            "test",
            Material.PAPER,
            "<rainbow>무지개색총공격이다!",
            mutableListOf(
                "<bold>굵은 로어"
            ),
            mutableMapOf(
                "key" to 1234
            )
        )

        repository.save(itemData)

        val findOne = repository.findById(itemData.id!!)
        assert(itemData == findOne)

        val findOneByName = repository.findByName(itemData.name)
        assert(itemData == findOneByName)
    }

    @Test
    fun findByName() {
        val findOne = repository.findByName("test")
        assert(findOne?.name == "test")
    }

    @Test
    fun findById() {
    }

    @Test
    fun delete() {
    }

    @Test
    fun getPrefix() {
    }
}