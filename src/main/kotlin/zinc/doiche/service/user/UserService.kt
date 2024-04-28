package zinc.doiche.service.user

import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.structure.Service
import zinc.doiche.service.common.CommonListener
import zinc.doiche.service.user.listener.UserIOListener
import zinc.doiche.service.user.repository.UserRepository

class UserService: Service {
    override fun onLoad() {
    }

    override fun onEnable() {
        plugin.register(UserIOListener())
    }

    override fun onDisable() {
    }

    companion object {
        val repository: UserRepository by lazyOf(UserRepository())

    }
}