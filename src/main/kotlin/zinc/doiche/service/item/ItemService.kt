package zinc.doiche.service.item

import zinc.doiche.lib.structure.Service
import zinc.doiche.service.item.repository.ItemDataRepository

class ItemService: Service {
    companion object {
        val repository: ItemDataRepository by lazyOf(ItemDataRepository())
    }

    override fun onLoad() {
    }

    override fun onEnable() {
    }

    override fun onDisable() {
    }
}