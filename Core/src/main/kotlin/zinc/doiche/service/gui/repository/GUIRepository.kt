package zinc.doiche.service.gui.repository

import zinc.doiche.database.repository.CachedKeyRepository
import zinc.doiche.service.gui.entity.GUI

class GUIRepository(override val prefix: String) : CachedKeyRepository<String, GUI>() {
    override fun save(entity: GUI) {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): GUI? {
        TODO("Not yet implemented")
    }

    override fun delete(entity: GUI) {
        TODO("Not yet implemented")
    }
}