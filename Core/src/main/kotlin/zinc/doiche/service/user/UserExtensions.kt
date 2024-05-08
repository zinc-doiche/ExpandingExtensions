package zinc.doiche.service.user

import org.bukkit.entity.Player
import zinc.doiche.service.user.entity.User

internal val Player.user: User?
    get() = UserService.repository.findByPlayer(this)