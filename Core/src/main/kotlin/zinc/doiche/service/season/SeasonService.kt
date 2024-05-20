package zinc.doiche.service.season

import zinc.doiche.service.Service
import zinc.doiche.service.season.manager.SeasonManager

class SeasonService: Service {
    companion object {
        val seasonManager: SeasonManager by lazy {
            SeasonManager()
        }
    }

    override fun onLoad() {

    }

    override fun onEnable() {
    }

    override fun onDisable() {
    }
}