package zinc.doiche.service.item

import org.bukkit.Material
import zinc.doiche.service.Service
import zinc.doiche.service.item.`object`.ItemData
import zinc.doiche.service.item.repository.ItemDataRepository

class ItemDataService: Service {
    companion object {
        val repository: ItemDataRepository by lazyOf(ItemDataRepository("item"))
    }

    override fun onLoad() {
    }

    override fun onEnable() {
        val data = ItemData(
            "test",
            Material.PAPER,
            "<aqua><b>DISPLAY",
            mutableListOf("<red>LORE"),
            mutableMapOf("key" to "value")
        )
        repository.save(data)
        val result = repository.findById(data.id!!)
        assert(result == data)
    }

    override fun onDisable() {
    }
}