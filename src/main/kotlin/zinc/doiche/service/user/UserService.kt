package zinc.doiche.service.user

import zinc.doiche.lib.structure.Service
import zinc.doiche.service.user.repository.UserRepository

class UserService: Service {
    companion object {
        val repository: UserRepository by lazyOf(UserRepository())

    }

    override fun onLoad() {
    }

    override fun onEnable() {
    }

    override fun onDisable() {
    }

}